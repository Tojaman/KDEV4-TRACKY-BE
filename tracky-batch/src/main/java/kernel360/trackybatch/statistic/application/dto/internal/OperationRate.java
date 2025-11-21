package kernel360.trackybatch.statistic.application.dto.internal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OperationRate(
        Long bizId,
        Double operationRate
) {
    public static Map<Long, Double> toMap(List<OperationRate> dtoList) {
        return dtoList.stream()
                .collect(Collectors.toMap(
                        OperationRate::bizId,
                        OperationRate::operationRate
                ));
    }
}
