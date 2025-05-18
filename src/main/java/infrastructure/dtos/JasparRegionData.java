package infrastructure.dtos;

import java.util.List;

public record JasparRegionData(
        String downloadTime,
        long downloadTimeStamp,
        String genome,
        String trackType,
        String track,
        String chrom,
        long chromSize,
        String bigDataUrl,
        long start,
        long end,
        List<JasparTranscriptionFactor> jaspar2022,
        int itemsReturned
) {
    public record JasparTranscriptionFactor(
            String chrom,
            int chromStart,
            int chromEnd,
            String name,
            int score,
            String strand,
            String TFName
    ) {}

    public List<JasparTranscriptionFactor> getTranscriptionFactors() { return jaspar2022; }
}
