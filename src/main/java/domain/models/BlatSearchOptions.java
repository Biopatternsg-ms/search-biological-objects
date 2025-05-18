package domain.models;

import lombok.Builder;

@Builder
public record BlatSearchOptions(
        double identity,
        String chromosome,
        String strand,
        String start,
        String end
) {
}
