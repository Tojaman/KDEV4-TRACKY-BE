package kernel360.trackybatch.car.infrastructure.repository;

import java.util.List;
import kernel360.trackybatch.car.application.dto.internal.CarCountWithBizId;

public interface CarDomainRepositoryCustom {

    List<CarCountWithBizId> getDailyTotalCarCount();
}
