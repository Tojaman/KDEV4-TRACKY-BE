package kernel360.trackybatch.statistic.infrastructure.repository.daily;

import static kernel360.trackycore.core.domain.entity.QBizEntity.bizEntity;
import static kernel360.trackycore.core.domain.entity.QDailyStatisticEntity.dailyStatisticEntity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kernel360.trackybatch.statistic.application.dto.internal.OperationCount;
import kernel360.trackybatch.statistic.application.dto.internal.OperationDistance;
import kernel360.trackybatch.statistic.application.dto.internal.OperationRate;
import kernel360.trackybatch.statistic.application.dto.internal.OperationTime;
import kernel360.trackybatch.statistic.application.dto.internal.TotalCarCount;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DailyStatisticRepositoryCustomImpl implements DailyStatisticRepositoryCustom {

        private final JPAQueryFactory queryFactory;

        // 말일의 total car count 값
        @Override
        public List<TotalCarCount> getLastTotalCarCount(LocalDate targetDate) {
                return queryFactory
                                .select(Projections.constructor(
                                                TotalCarCount.class,
                                                bizEntity.id,
                                                dailyStatisticEntity.totalCarCount.coalesce(0)))
                                .from(bizEntity)
                                .leftJoin(dailyStatisticEntity).on(
                                                dailyStatisticEntity.bizId.eq(bizEntity.id),
                                                dailyStatisticEntity.date.eq(targetDate))
                                .groupBy(bizEntity.id, dailyStatisticEntity.totalCarCount) // 수정
                                .fetch();
        }

        // 차량 가동률의 평균
        @Override
        public List<OperationRate> findAverageOperationRate(LocalDate targetDate) {
                LocalDate firstDay = targetDate.withDayOfMonth(1);

                return queryFactory
                                .select(Projections.constructor(
                                                OperationRate.class,
                                                bizEntity.id,
                                                dailyStatisticEntity.avgOperationRate.avg().coalesce(0.0)))
                                .from(bizEntity)
                                .leftJoin(dailyStatisticEntity).on(
                                                dailyStatisticEntity.bizId.eq(bizEntity.id),
                                                dailyStatisticEntity.date.between(firstDay, targetDate))
                                .groupBy(bizEntity.id)
                                .fetch();

        }

        // 차량 운행 횟수의 총 합계
        @Override
        public List<OperationCount> findSumOperationCount(LocalDate targetDate) {
                LocalDate firstDay = targetDate.withDayOfMonth(1);

                return queryFactory
                                .select(Projections.constructor(
                                                OperationCount.class,
                                                bizEntity.id,
                                                dailyStatisticEntity.dailyDriveCount.sum().coalesce(0)))
                                .from(bizEntity)
                                .leftJoin(dailyStatisticEntity).on(
                                                dailyStatisticEntity.bizId.eq(bizEntity.id),
                                                dailyStatisticEntity.date.between(firstDay, targetDate))
                                .groupBy(bizEntity.id)
                                .fetch();
        }

        // 차량 운행 시간의 총 합계
        @Override
        public List<OperationTime> findSumOperationTime(LocalDate targetDate) {
                LocalDate firstDay = targetDate.withDayOfMonth(1);

                return queryFactory
                                .select(Projections.constructor(
                                                OperationTime.class,
                                                bizEntity.id,
                                                dailyStatisticEntity.dailyDriveSec.sum().coalesce(0L)))
                                .from(bizEntity)
                                .leftJoin(dailyStatisticEntity).on(
                                                dailyStatisticEntity.bizId.eq(bizEntity.id),
                                                dailyStatisticEntity.date.between(firstDay, targetDate))
                                .groupBy(bizEntity.id)
                                .fetch();
        }

        // 차량 운행 거리의 총 합계
        @Override
        public List<OperationDistance> findSumOperationDistance(LocalDate targetDate) {
                LocalDate firstDay = targetDate.withDayOfMonth(1);

                return queryFactory
                                .select(Projections.constructor(
                                                OperationDistance.class,
                                                bizEntity.id,
                                                dailyStatisticEntity.dailyDriveDistance.sum().coalesce(0.0)))
                                .from(bizEntity)
                                .leftJoin(dailyStatisticEntity).on(
                                                dailyStatisticEntity.bizId.eq(bizEntity.id),
                                                dailyStatisticEntity.date.between(firstDay, targetDate))
                                .groupBy(bizEntity.id)
                                .fetch();
        }
}
