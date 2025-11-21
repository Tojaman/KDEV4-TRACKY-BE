package kernel360.trackybatch.timedistance.infrastructure.repository;

import static kernel360.trackycore.core.domain.entity.QBizEntity.bizEntity;
import static kernel360.trackycore.core.domain.entity.QTimeDistanceEntity.timeDistanceEntity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kernel360.trackybatch.timedistance.application.dto.internal.OperationDistance;
import kernel360.trackybatch.timedistance.application.dto.internal.OperationSeconds;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TimeDistanceDomainRepositoryCustomImpl implements TimeDistanceDomainRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OperationSeconds> getDailyOperationTime(LocalDate targetDate) {
        return queryFactory
                .select(Projections.constructor(
                        OperationSeconds.class,
                        bizEntity.id,
                        timeDistanceEntity.seconds.sum().coalesce(0) // 운행 시간이 없을 경우 0으로 처리
                ))
                .from(bizEntity)
                .leftJoin(timeDistanceEntity).on(
                        timeDistanceEntity.biz.id.eq(bizEntity.id),
                        timeDistanceEntity.date.eq(targetDate)
                )
                .groupBy(bizEntity.id)
                .fetch();
    }

    @Override
    public List<OperationDistance> getDailyOperationDistance(LocalDate targetDate) {

        return queryFactory
                .select(Projections.constructor(
                        OperationDistance.class,
                        bizEntity.id,
                        timeDistanceEntity.distance.sum().coalesce(0.0)
                ))
                .from(bizEntity)
                .leftJoin(timeDistanceEntity)
                .on(
                        timeDistanceEntity.biz.id.eq(bizEntity.id),
                        timeDistanceEntity.date.eq(targetDate)
                )
                .groupBy(bizEntity.id)
                .fetch();
    }
}
