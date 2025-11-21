package kernel360.trackybatch.statistic.application.dto.internal;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record TotalCarCount(
        Long bizId,
        int totalCarCount
) {
    public static Map<Long, Integer> toMap(List<TotalCarCount> dtoList) {
        return dtoList.stream()
                .collect(Collectors.toMap(
                        TotalCarCount::bizId,
                        TotalCarCount::totalCarCount
                ));
    }
}
