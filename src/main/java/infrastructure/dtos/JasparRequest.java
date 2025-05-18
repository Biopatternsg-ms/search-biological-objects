package infrastructure.dtos;

public record JasparRequest(
        String genome,
        String track,
        String chromosome,
        String start,
        String end,
        String strand,
        int reliability
) {
}
