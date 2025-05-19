package infrastructure.dtos;

import jakarta.ws.rs.QueryParam;
import lombok.Builder;

@Builder
public class JasparRegionDataRequest {
        @QueryParam("genome") public String genome;
        @QueryParam("track") public String track;
        @QueryParam("chrom") public String chrom;
        @QueryParam("start") public String start;
        @QueryParam("end") public String end;

        public static JasparRegionDataRequest of(JasparTFRequest jasparRequest) {
                return JasparRegionDataRequest.builder()
                        .genome(jasparRequest.genome())
                        .track(jasparRequest.track())
                        .chrom(jasparRequest.chromosome())
                        .start(jasparRequest.start())
                        .end(jasparRequest.end())
                        .build();
        }
}