package kernel360.trackybatch.statistic.infrastructure.repository.daily;

import java.time.LocalDate;
import java.util.List;

import kernel360.trackybatch.statistic.application.dto.internal.OperationCount;
import kernel360.trackybatch.statistic.application.dto.internal.OperationDistance;
import kernel360.trackybatch.statistic.application.dto.internal.OperationRate;
import kernel360.trackybatch.statistic.application.dto.internal.OperationTime;
import kernel360.trackybatch.statistic.application.dto.internal.TotalCarCount;

public interface DailyStatisticRepositoryCustom {

    List<TotalCarCount> getLastTotalCarCount(LocalDate targetDate);

    List<OperationRate> findAverageOperationRate(LocalDate targetDate);

    List<OperationCount> findSumOperationCount(LocalDate targetDate);

    List<OperationTime> findSumOperationTime(LocalDate targetDate);

    List<OperationDistance> findSumOperationDistance(LocalDate targetDate);
}
