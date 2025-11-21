package kernel360.trackybatch.car.infrastructure.repository;

import static kernel360.trackycore.core.domain.entity.QBizEntity.bizEntity;
import static kernel360.trackycore.core.domain.entity.QCarEntity.carEntity;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kernel360.trackybatch.car.application.dto.internal.CarCountWithBizId;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CarDomainRepositoryCustomImpl implements CarDomainRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CarCountWithBizId> getDailyTotalCarCount() {
        return queryFactory
                .select(Projections.constructor(
                        CarCountWithBizId.class,
                        bizEntity.id,
                        carEntity.count().coalesce(0L) // car가 없을 경우 0으로 처리
                ))
                .from(bizEntity)
                .leftJoin(carEntity).on(carEntity.biz.id.eq(bizEntity.id))
                .groupBy(bizEntity.id)
                .fetch();
    }
}
