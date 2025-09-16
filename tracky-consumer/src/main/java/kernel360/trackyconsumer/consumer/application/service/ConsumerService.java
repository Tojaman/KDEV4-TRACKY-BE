package kernel360.trackyconsumer.consumer.application.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.LocalDate;
import java.util.List;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kernel360.trackyconsumer.consumer.application.dto.request.CarOnOffRequest;
import kernel360.trackyconsumer.consumer.application.dto.request.CycleGpsRequest;
import kernel360.trackyconsumer.consumer.application.dto.request.GpsHistoryMessage;
import kernel360.trackyconsumer.drive.domain.provider.DriveDomainProvider;
import kernel360.trackyconsumer.rent.domain.provider.RentDomainProvider;
import kernel360.trackyconsumer.timedistance.domain.provider.TimeDistanceDomainProvider;
import kernel360.trackycore.core.domain.entity.CarEntity;
import kernel360.trackycore.core.domain.entity.DriveEntity;
import kernel360.trackycore.core.domain.entity.GpsHistoryEntity;
import kernel360.trackycore.core.domain.entity.LocationEntity;
import kernel360.trackycore.core.domain.entity.RentEntity;
import kernel360.trackycore.core.domain.entity.TimeDistanceEntity;
import kernel360.trackycore.core.domain.provider.CarProvider;
import kernel360.trackycore.core.domain.provider.GpsHistoryProvider;
import kernel360.trackycore.core.domain.provider.LocationProvider;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConsumerService {

	private final DriveDomainProvider driveProvider;
	private final CarProvider carProvider;
	private final LocationProvider locationProvider;
	private final GpsHistoryProvider gpsHistoryProvider;
	private final RentDomainProvider rentDomainProvider;
	private final TimeDistanceDomainProvider timeDistanceDomainProvider;
	private final Timer timeDistanceProcessingTimer;

	public ConsumerService(DriveDomainProvider driveProvider, CarProvider carProvider,
		LocationProvider locationProvider, GpsHistoryProvider gpsHistoryProvider,
		RentDomainProvider rentDomainProvider,
		TimeDistanceDomainProvider timeDistanceDomainProvider, MeterRegistry meterRegistry) {
		this.driveProvider = driveProvider;
		this.carProvider = carProvider;
		this.locationProvider = locationProvider;
		this.gpsHistoryProvider = gpsHistoryProvider;
		this.rentDomainProvider = rentDomainProvider;
		this.timeDistanceDomainProvider = timeDistanceDomainProvider;
		this.timeDistanceProcessingTimer = Timer.builder("time.distance.processing")
			.description("시간/거리 계산 처리 시간")
			.publishPercentileHistogram()
			.register(meterRegistry);
	}

	@Transactional
	public void receiveCycleInfo(GpsHistoryMessage request) {
		List<CycleGpsRequest> cycleGpsRequestList = request.cList();

		CarEntity car = carProvider.findByMdn(request.mdn()); // 캐싱 도입
		DriveEntity drive = driveProvider.getDrive(car, request.oTime());

		drive.skipCount(removeOverDistance(cycleGpsRequestList));

		if (!cycleGpsRequestList.isEmpty()) {
			processTimeDistance(cycleGpsRequestList, car);
		}

		List<GpsHistoryEntity> gpsHistories = toGpsHistoryList(cycleGpsRequestList, drive);
		gpsHistoryProvider.saveAll(gpsHistories);
	}

	@Transactional
	public List<GpsHistoryEntity> receiveCycleInfo_bulk(GpsHistoryMessage request) {
		List<CycleGpsRequest> cycleGpsRequestList = request.cList();

		CarEntity car = carProvider.findByMdn(request.mdn()); // 캐싱 도입
		DriveEntity drive = driveProvider.getDrive(car, request.oTime());

		drive.skipCount(removeOverDistance(cycleGpsRequestList));

		if (!cycleGpsRequestList.isEmpty()) {
			processTimeDistance(cycleGpsRequestList, car);
		}

		return toGpsHistoryList(cycleGpsRequestList, drive);
	}

	public void saveAllGps(List<GpsHistoryEntity> gpsHistories) {
		gpsHistoryProvider.saveAll(gpsHistories);
	}

	@Transactional
	public void processOnMessage(CarOnOffRequest carOnOffRequest) {

		LocationEntity location = LocationEntity.create(carOnOffRequest.gpsInfo().getLon(),
			carOnOffRequest.gpsInfo().getLat());
		locationProvider.save(location);

		CarEntity car = carProvider.findByMdn(carOnOffRequest.mdn());
		car.updateStatusToRunning();

		RentEntity rent = rentDomainProvider.getRent(car, carOnOffRequest.onTime());

		DriveEntity drive = DriveEntity.create(car, rent, location,
			carOnOffRequest.onTime());

		driveProvider.save(drive);
	}

	@Transactional
	public void processOffMessage(CarOnOffRequest carOnOffRequest) {

		CarEntity car = carProvider.findByMdn(carOnOffRequest.mdn());
		DriveEntity drive = driveProvider.getDrive(car, carOnOffRequest.onTime());

		drive.off(carOnOffRequest.gpsInfo().getSum(), carOnOffRequest.offTime());
		car.off(drive.getDriveDistance(), carOnOffRequest.offTime());

		LocationEntity location = drive.getLocation();
		location.updateEndLocation(carOnOffRequest.gpsInfo().getLat(), carOnOffRequest.gpsInfo().getLon());
	}

	private List<GpsHistoryEntity> toGpsHistoryList(List<CycleGpsRequest> cycleGpsRequestList,
		DriveEntity drive) {

		return cycleGpsRequestList.stream()
			.map(request -> request.toGpsHistoryEntity(drive, request.gpsInfo().getSum()))
			.toList();
	}

	private int removeOverDistance(List<CycleGpsRequest> cycleGpsRequests) {

		int count = 0;
		for (int i = cycleGpsRequests.size() - 1; i >= 0; i--) {
			if (cycleGpsRequests.get(i).gpsInfo().getSum() > 80) {
				cycleGpsRequests.remove(i);
				count++;
			}
		}
		return count;
	}

	/**
	 * 주기 정보(60개)를 순회하며 TimeDistance를 저장한다.
	 * 만약 시간이 바뀌었다면 바뀐 시간으로 TimeDistance를 저장한다.(if (nowHour != newHour))
	 * DB 접근을 최소화하기 위해 움직이지 않은 경우엔 저장하지 않는다.(if (distance > 0.0))
	 */
	private void processTimeDistance(List<CycleGpsRequest> cycleGpsRequests, CarEntity car) {

		double distance = 0.0;
		int seconds = 1;
		LocalDate prevDate = cycleGpsRequests.get(0).oTime().toLocalDate();
		int prevHour = cycleGpsRequests.get(0).oTime().getHour();

		for (CycleGpsRequest cycleGpsRequest : cycleGpsRequests) {

			LocalDate currentDate = cycleGpsRequest.oTime().toLocalDate();
			int currentHour = cycleGpsRequest.oTime().getHour();

			if (prevHour != currentHour) {
				if (distance > 0.0)
					saveTimeDistance(prevDate, prevHour, car, distance, seconds);
				distance = 0.0;
				prevDate = currentDate;
				prevHour = currentHour;
			}

			distance += cycleGpsRequest.gpsInfo().getSum();
			seconds++;
		}

		if (distance > 0.0)
			saveTimeDistance(prevDate, prevHour, car, distance, seconds);
	}

	private void saveTimeDistance(LocalDate date, int hour, CarEntity car, double totalDistance, int seconds) {

		// SELECT -> INSERT or UPDATE
//		timeDistanceProcessingTimer.record(() -> {
//			long start = System.currentTimeMillis();
//			timeDistanceDomainProvider.getTimeDistance(date, hour, car)
//				.ifPresentOrElse(
//					timeDistance -> timeDistance.updateDistance(totalDistance, seconds),
//					() -> timeDistanceDomainProvider.save(
//						TimeDistanceEntity.create(car, car.getBiz(), date, hour, totalDistance, seconds)
//					)
//				);
//			long end = System.currentTimeMillis();
//			long duration = end - start;
//			log.info("processTimeDistance 처리 완료 | 소요 시간: {}ms", duration);
//		});

		// UPSERT
		timeDistanceProcessingTimer.record(() -> {
			long start = System.currentTimeMillis();

			timeDistanceDomainProvider.upsert(TimeDistanceEntity.create(car, car.getBiz(), date, hour, totalDistance, seconds));

			long end = System.currentTimeMillis();
			long duration = end - start;
			log.info("processTimeDistance 처리 완료 | 소요 시간: {}ms", duration);
		});
	}
}
