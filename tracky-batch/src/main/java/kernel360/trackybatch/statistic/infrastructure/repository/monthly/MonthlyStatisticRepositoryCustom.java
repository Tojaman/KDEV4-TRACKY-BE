package kernel360.trackybatch.statistic.infrastructure.repository.monthly;

import java.time.LocalDate;

import kernel360.trackycore.core.domain.entity.MonthlyStatisticEntity;

public interface MonthlyStatisticRepositoryCustom {

    MonthlyStatisticEntity findByBizIdAndDate(Long bizId, LocalDate date);
}
