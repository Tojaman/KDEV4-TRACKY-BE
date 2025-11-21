package kernel360.trackybatch.statistic.application.dto.internal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OperationTime(
        Long bizId,
        Long operationTime
) {
    public static Map<Long, Long> toMap(List<OperationTime> dtoList) {
        return dtoList.stream()
                .collect(Collectors.toMap(
                        OperationTime::bizId,
                        OperationTime::operationTime
                ));
    }
}
