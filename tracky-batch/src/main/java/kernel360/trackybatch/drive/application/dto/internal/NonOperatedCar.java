package kernel360.trackybatch.drive.application.dto.internal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record NonOperatedCar(
        Long bizId,
        Long nonOperatedCar
) {
    public static Map<Long, Integer> toMap(List<NonOperatedCar> dtoList) {
        return dtoList.stream()
                .collect(Collectors.toMap(
                        NonOperatedCar::bizId,
                        dto -> dto.nonOperatedCar().intValue() // Long → int 변환

                ));
    }
}
