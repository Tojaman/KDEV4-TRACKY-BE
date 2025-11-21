package kernel360.trackybatch.drive.application.dto.internal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OperationCarCount(
        Long bizId,
        Long carCount) {

    public static Map<Long, Integer> toMap(List<OperationCarCount> dtoList) {
        return dtoList.stream()
                .collect(Collectors.toMap(
                        OperationCarCount::bizId,
                        dto -> dto.carCount().intValue() // Long → int 변환
                ));
    }
}
