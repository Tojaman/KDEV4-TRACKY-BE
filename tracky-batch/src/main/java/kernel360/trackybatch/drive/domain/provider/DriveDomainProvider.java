package kernel360.trackybatch.drive.domain.provider;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Component;

import kernel360.trackybatch.drive.application.dto.internal.NonOperatedCar;
import kernel360.trackybatch.drive.application.dto.internal.OperationCarCount;
import kernel360.trackybatch.drive.application.dto.internal.OperationTotalCount;
import kernel360.trackybatch.drive.infrastructure.repository.DriveDomainRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DriveDomainProvider {

    private final DriveDomainRepository driveDomainRepository;

    //일일 통계 - 당일 운행 차량 수 (distinct mdn)
    public Map<Long, Integer> countDailyOperationCar(LocalDate targetDate) {
        return OperationCarCount.toMap(driveDomainRepository.getDailyOperationCar(targetDate));
    }

    //일일 통계 - 당일 총 운행 수
    public Map<Long, Integer> countDailyTotalOperation(LocalDate targetDate) {
        return OperationTotalCount.toMap(driveDomainRepository.getDailyTotalOperation(targetDate));
    }

    //월별 통계 - 미운행 차량 수
    public Map<Long, Integer> getNonOperatedCars(LocalDate targetDate) {
        return NonOperatedCar.toMap(driveDomainRepository.getNonOperatedCars(targetDate));
    }
}
