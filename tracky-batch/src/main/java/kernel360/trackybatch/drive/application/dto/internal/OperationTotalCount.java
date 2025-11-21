package kernel360.trackybatch.drive.application.dto.internal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OperationTotalCount(
        Long bizId,
        Long totalCount
) {
    public static Map<Long, Integer> toMap(List<OperationTotalCount> dtoList) {
        return dtoList.stream()
                .collect(Collectors.toMap(
                        OperationTotalCount::bizId,
                        dto -> dto.totalCount().intValue()
                ));
    }
}
