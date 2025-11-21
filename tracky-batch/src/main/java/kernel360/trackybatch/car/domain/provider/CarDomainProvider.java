package kernel360.trackybatch.car.domain.provider;

import java.util.Map;

import org.springframework.stereotype.Component;

import kernel360.trackybatch.car.application.dto.internal.CarCountWithBizId;
import kernel360.trackybatch.car.infrastructure.repository.CarDomainRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CarDomainProvider {

    private final CarDomainRepository carDomainRepository;

    public Map<Long, Integer> countDailyTotalCar() {
        return CarCountWithBizId.toMap(carDomainRepository.getDailyTotalCarCount());
    }
}
