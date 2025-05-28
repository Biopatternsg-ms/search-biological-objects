package infrastructure.external_services.dtos.hgnc;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HGNCSymbol {
    private String symbol;
    private String name;
    private String status;
    private String locusGroup;
    private String locusType;
    private String chromosome;
    private String genomicStart;
    private String genomicEnd;
    private String orientation;
}
