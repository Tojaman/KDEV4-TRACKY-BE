package kernel360.trackybatch.car.application.dto.internal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record CarCountWithBizId(
        Long bizId,
        Long carCount
) {
    public static Map<Long, Integer> toMap(List<CarCountWithBizId> dtoList) {
        return dtoList.stream()
                .collect(Collectors.toMap(
                        CarCountWithBizId::bizId,
                        dto -> dto.carCount().intValue() // Long → int 변환
                ));
    }
}
