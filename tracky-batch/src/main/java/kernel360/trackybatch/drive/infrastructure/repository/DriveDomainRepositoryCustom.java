package kernel360.trackybatch.drive.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

import kernel360.trackybatch.drive.application.dto.internal.NonOperatedCar;
import kernel360.trackybatch.drive.application.dto.internal.OperationCarCount;
import kernel360.trackybatch.drive.application.dto.internal.OperationTotalCount;

public interface DriveDomainRepositoryCustom {

    List<OperationCarCount> getDailyOperationCar(LocalDate targetDate);

    List<OperationTotalCount> getDailyTotalOperation(LocalDate targetDate);

    List<NonOperatedCar> getNonOperatedCars(LocalDate targetDate);
}
