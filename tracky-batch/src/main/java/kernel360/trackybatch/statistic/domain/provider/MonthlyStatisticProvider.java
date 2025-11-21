package kernel360.trackybatch.statistic.domain.provider;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import kernel360.trackycore.core.domain.entity.MonthlyStatisticEntity;
import kernel360.trackybatch.statistic.infrastructure.repository.monthly.MonthlyStatisticDomainRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MonthlyStatisticProvider {

    private final MonthlyStatisticDomainRepository monthlyStatisticRepository;

    @Transactional
    public void saveMonthlyStatistic(List<MonthlyStatisticEntity> resultEntities) {

        for (MonthlyStatisticEntity entity : resultEntities) {
            MonthlyStatisticEntity existEntity = monthlyStatisticRepository.findByBizIdAndDate(
                    entity.getBizId(), entity.getDate());

            if (existEntity != null) {
                existEntity.update(entity.getDate(), entity.getTotalCarCount(), entity.getNonOperatingCarCount(),
                        entity.getAvgOperationRate(), entity.getTotalDriveSec(), entity.getTotalDriveCount(),
                        entity.getTotalDriveDistance());
            } else {
                monthlyStatisticRepository.save(entity);
            }
        }
    }
}
