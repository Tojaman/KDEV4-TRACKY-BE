package kernel360.trackybatch.statistic.application.dto.internal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OperationCount (
        Long bizId,
        int operationCount
){
    public static Map<Long, Integer> toMap(List<OperationCount> dtoList) {
        return dtoList.stream()
                .collect(Collectors.toMap(
                        OperationCount::bizId,
                        OperationCount::operationCount
                ));
    }
}
