package kernel360.trackybatch.statistic.application.dto.internal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OperationDistance(
        Long bizId,
        Double operationDistance
) {
    public static Map<Long, Double> toMap(List<OperationDistance> dtoList) {
        return dtoList.stream()
                .collect(Collectors.toMap(
                        OperationDistance::bizId,
                        OperationDistance::operationDistance
                ));
    }
}
