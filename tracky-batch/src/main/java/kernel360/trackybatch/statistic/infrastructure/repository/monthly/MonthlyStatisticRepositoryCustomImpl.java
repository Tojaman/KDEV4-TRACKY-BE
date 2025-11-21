package kernel360.trackybatch.statistic.infrastructure.repository.monthly;

import static kernel360.trackycore.core.domain.entity.QMonthlyStatisticEntity.monthlyStatisticEntity;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import kernel360.trackycore.core.domain.entity.MonthlyStatisticEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class MonthlyStatisticRepositoryCustomImpl implements MonthlyStatisticRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public MonthlyStatisticEntity findByBizIdAndDate(Long bizId, LocalDate date) {
        LocalDate start = date.withDayOfMonth(1);
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());

        return queryFactory
                .selectFrom(monthlyStatisticEntity)
                .where(monthlyStatisticEntity.biz.id.eq(bizId)
                        .and(monthlyStatisticEntity.date.between(start, end))
                )
                .fetchOne();
    }
}
