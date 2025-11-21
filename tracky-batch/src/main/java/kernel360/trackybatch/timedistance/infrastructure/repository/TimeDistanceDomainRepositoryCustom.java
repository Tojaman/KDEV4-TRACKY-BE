package kernel360.trackybatch.timedistance.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

import kernel360.trackybatch.timedistance.application.dto.internal.OperationDistance;
import kernel360.trackybatch.timedistance.application.dto.internal.OperationSeconds;

public interface TimeDistanceDomainRepositoryCustom {

    List<OperationSeconds> getDailyOperationTime(LocalDate targetDate);

    List<OperationDistance> getDailyOperationDistance(LocalDate targetDate);
}
