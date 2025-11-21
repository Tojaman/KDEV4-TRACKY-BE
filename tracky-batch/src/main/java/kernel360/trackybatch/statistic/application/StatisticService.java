package kernel360.trackybatch.statistic.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kernel360.trackycore.core.domain.entity.DailyStatisticEntity;
import kernel360.trackycore.core.domain.entity.MonthlyStatisticEntity;
import kernel360.trackybatch.car.domain.provider.CarDomainProvider;
import kernel360.trackybatch.drive.domain.provider.DriveDomainProvider;
import kernel360.trackybatch.statistic.domain.provider.DailyStatisticProvider;
import kernel360.trackybatch.statistic.domain.provider.MonthlyStatisticProvider;
import kernel360.trackybatch.timedistance.domain.provider.TimeDistanceDomainProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticService {

    private final CarDomainProvider carDomainProvider;
    private final DriveDomainProvider driveDomainProvider;
    private final TimeDistanceDomainProvider timeDistanceDomainProvider;
    private final DailyStatisticProvider dailyStatisticProvider;
    private final MonthlyStatisticProvider monthlyStatisticProvider;

    //일별 통계
    public void dailyStatistic(LocalDate targetDate) {
        //업체별 차량 총 개수
        Map<Long, Integer> totalCars = carDomainProvider.countDailyTotalCar();
        log.info("차량 총 개수: {}", totalCars.toString());
        //일일 운행 차량 수
        Map<Long, Integer> totalOperationCars = driveDomainProvider.countDailyOperationCar(targetDate);
        log.info("일일 운행 차량 수: {}", totalOperationCars.toString());
        //일일 가동률
        Map<Long, Double> totalOperationRates = calculateDailyOperationRate(totalCars, totalOperationCars);
        log.info("일일 가동률: {}", totalOperationRates.toString());
        //일일 총 운행 시간
        Map<Long, Long> totalOperationSeconds = timeDistanceDomainProvider.calculateDailyOperationTime(targetDate);
        log.info("일일 총 운행 시간: {}", totalOperationSeconds.toString());
        //일일 총 운행 횟수
        Map<Long, Integer> totalOperationCounts = driveDomainProvider.countDailyTotalOperation(targetDate);
        log.info("일일 총 운행 횟수: {}", totalOperationCounts.toString());
        //일일 총 운행 거리
        Map<Long, Double> totalOperationDistances = timeDistanceDomainProvider.calculateDailyOperationDistance(
                targetDate);
        log.info("일일 총 운행 거리: {}", totalOperationDistances.toString());

        //결과 엔티티 리스트로 변환
        List<DailyStatisticEntity> resultEntities = getResultDailyStatisticEntity(
                targetDate, totalCars, totalOperationCars, totalOperationRates, totalOperationSeconds,
                totalOperationCounts, totalOperationDistances);

        //저장
        dailyStatisticProvider.saveDailyStatistic(resultEntities);
    }

    //월별 통계
    public void monthlyStatistic(LocalDate targetDate) {
        //전체 보유 차량(말일 기준 보유 차량)
        Map<Long, Integer> totalCars = dailyStatisticProvider.getLastTotalCarCount(targetDate);
        //미운행 차량 대수
        Map<Long, Integer> totalUnoperatedCars = driveDomainProvider.getNonOperatedCars(targetDate);
        //월 평균 가동률
        Map<Long, Double> avgMonthlyOperationRates = dailyStatisticProvider.getOperationRatesAvg(targetDate);
        //월 총 운행량
        Map<Long, Integer> avgMonthlyOperationCounts = dailyStatisticProvider.getOperationCountsTotal(targetDate);
        //월 총 운행 시간
        Map<Long, Long> avgMonthlyOperationTimes = dailyStatisticProvider.getOperationTimesTotal(targetDate);
        //월 총 운행 거리
        Map<Long, Double> avgMonthlyOperationDistances = dailyStatisticProvider.getOperationDistancesTotal(targetDate);

        //결과 엔티티 리스트로 변환
        List<MonthlyStatisticEntity> resultEntities = getResultMonthlyStatisticEntity(
                targetDate, totalCars, totalUnoperatedCars, avgMonthlyOperationRates, avgMonthlyOperationCounts,
                avgMonthlyOperationTimes, avgMonthlyOperationDistances);

        //저장
        monthlyStatisticProvider.saveMonthlyStatistic(resultEntities);
    }

    //일별 가동률 계산
    private Map<Long, Double> calculateDailyOperationRate(Map<Long, Integer> totalCarMap,
                                                          Map<Long, Integer> operationCarMap) {
        Map<Long, Double> result = new HashMap<>();

        // totalCarMap의 모든 bizId에 대해 반복
        for (Map.Entry<Long, Integer> entry : totalCarMap.entrySet()) {
            Long bizId = entry.getKey();
            int totalCarCount = entry.getValue();

            // 해당 bizId에 대한 운행 차량 수 가져오기 (없으면 0)
            int operationCarCount = operationCarMap.getOrDefault(bizId, 0);

            // 가동률 계산 (총 차량이 0인 경우 0%로 설정)
            double operationRate = totalCarCount > 0 ? (double)operationCarCount / totalCarCount * 100 : 0;

            result.put(bizId, operationRate);
        }
        return result;
    }

    //Daily Statistic Entity List로 변환
    private List<DailyStatisticEntity> getResultDailyStatisticEntity(
            LocalDate targetDate, Map<Long, Integer> totalCarMap, Map<Long, Integer> operationCarMap,
            Map<Long, Double> operationRates, Map<Long, Long> operationSecondsMap,
            Map<Long, Integer> operationTotalCountsMap, Map<Long, Double> operationTotalDistanceMap) {

        List<DailyStatisticEntity> resultEntities = new ArrayList<>();
        for (Long bizId : totalCarMap.keySet()) {
            int totalCar = totalCarMap.getOrDefault(bizId, 0);
            int operationCar = operationCarMap.getOrDefault(bizId, 0);
            double operationRate = operationRates.getOrDefault(bizId, 0.0);
            long operationSeconds = operationSecondsMap.getOrDefault(bizId, 0L);
            int operationCount = operationTotalCountsMap.getOrDefault(bizId, 0);
            double distance = operationTotalDistanceMap.getOrDefault(bizId, 0.0);

            DailyStatisticEntity stat = DailyStatisticEntity.create(bizId, targetDate, totalCar,
                    operationCar, operationRate, operationSeconds, operationCount, distance);
            resultEntities.add(stat);
        }
        return resultEntities;
    }

    //Monthly Statistic Entity List로 변환
    private List<MonthlyStatisticEntity> getResultMonthlyStatisticEntity(
            LocalDate targetDate, Map<Long, Integer> totalCars, Map<Long, Integer> totalUnoperatedCars,
            Map<Long, Double> avgMonthlyOperationRates, Map<Long, Integer> avgMonthlyOperationCounts,
            Map<Long, Long> avgMonthlyOperationTimes, Map<Long, Double> avgMonthlyOperationDistances) {

        List<MonthlyStatisticEntity> resultEntities = new ArrayList<>();
        for (Long bizId : totalCars.keySet()) {
            int totalCar = totalCars.getOrDefault(bizId, 0);
            int totalUnoperatedCar = totalUnoperatedCars.getOrDefault(bizId, 0);
            double avgMonthlyOperationRate = avgMonthlyOperationRates.getOrDefault(bizId, 0.0);
            int avgMonthlyOperationCount = avgMonthlyOperationCounts.getOrDefault(bizId, 0);
            long avgMonthlyOperationTime = avgMonthlyOperationTimes.getOrDefault(bizId, 0L);
            double avgMonthlyOperationDistance = avgMonthlyOperationDistances.getOrDefault(bizId, 0.0);

            MonthlyStatisticEntity stat = MonthlyStatisticEntity.create(bizId, targetDate, totalCar, totalUnoperatedCar,
                    avgMonthlyOperationRate, avgMonthlyOperationTime, avgMonthlyOperationCount,
                    avgMonthlyOperationDistance);

            resultEntities.add(stat);
        }

        return resultEntities;
    }
}
