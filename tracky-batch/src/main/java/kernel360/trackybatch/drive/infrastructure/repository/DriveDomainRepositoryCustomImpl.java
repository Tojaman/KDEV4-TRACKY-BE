package kernel360.trackybatch.drive.infrastructure.repository;

import static kernel360.trackycore.core.domain.entity.QBizEntity.bizEntity;
import static kernel360.trackycore.core.domain.entity.QCarEntity.carEntity;
import static kernel360.trackycore.core.domain.entity.QDriveEntity.driveEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kernel360.trackybatch.drive.application.dto.internal.NonOperatedCar;
import kernel360.trackybatch.drive.application.dto.internal.OperationCarCount;
import kernel360.trackybatch.drive.application.dto.internal.OperationTotalCount;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DriveDomainRepositoryCustomImpl implements DriveDomainRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    //일일 통계 - target Date에 운행한 차량 수
    @Override
    public List<OperationCarCount> getDailyOperationCar(LocalDate targetDate) {
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();

        return queryFactory
                .select(Projections.constructor(
                        OperationCarCount.class,
                        bizEntity.id,
                        driveEntity.car.mdn.countDistinct().coalesce(0L)
                ))
                .from(bizEntity)
                .leftJoin(carEntity).on(carEntity.biz.id.eq(bizEntity.id))
                .leftJoin(driveEntity).on(
                        driveEntity.car.mdn.eq(carEntity.mdn),
                        driveEntity.driveOnTime.between(start, end.minusNanos(1))
                )
                .groupBy(bizEntity.id)
                .fetch();
    }

    //일일 통계 - target Date의 운행 총 횟수
    @Override
    public List<OperationTotalCount> getDailyTotalOperation(LocalDate targetDate) {
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay().minusNanos(1);

        return queryFactory
                .select(Projections.constructor(
                        OperationTotalCount.class,
                        bizEntity.id,
                        driveEntity.count().coalesce(0L)
                ))
                .from(bizEntity)
                .leftJoin(driveEntity)
                .on(
                        driveEntity.car.biz.id.eq(bizEntity.id),
                        driveEntity.driveOnTime.between(start, end)
                )
                .groupBy(bizEntity.id)
                .fetch();
    }

    //월별 통계 - 미운행 차량 수
    @Override
    public List<NonOperatedCar> getNonOperatedCars(LocalDate targetDate) {
        LocalDateTime monthStart = targetDate.withDayOfMonth(1).atStartOfDay();
        LocalDateTime targetEnd = targetDate.plusDays(1).atStartOfDay();

        return queryFactory
                .select(Projections.constructor(
                        NonOperatedCar.class,
                        bizEntity.id,
                        carEntity.count().coalesce(0L)
                ))
                .from(bizEntity)
                .leftJoin(carEntity).on(carEntity.biz.id.eq(bizEntity.id))
                .leftJoin(driveEntity).on(
                        driveEntity.car.eq(carEntity),
                        driveEntity.driveOnTime.between(monthStart, targetEnd)
                )
                .where(driveEntity.isNull())
                .groupBy(bizEntity.id)
                .fetch();
    }
}
