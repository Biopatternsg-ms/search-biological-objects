package infrastructure.adapters.out;

import domain.enums.TranscriptionFactorSource;
import infrastructure.adapters.in.JasparRegionDataFetcher;
import infrastructure.dtos.ConsultaJaspar;
import infrastructure.dtos.JasparRequest;
import infrastructure.dtos.JasparRegionData;
import infrastructure.dtos.PromoterRegionDTO;
import domain.models.BlatSearchOptions;
import domain.models.TranscriptionFactor;
import domain.port.TranscriptionFactorJasparProviderPort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

@Slf4j
@ApplicationScoped
public class TranscriptionFactorJasparDataProviderAdapter implements TranscriptionFactorJasparProviderPort {

    private final JasparRegionDataFetcher jasparRegionDataFetcher;

    public TranscriptionFactorJasparDataProviderAdapter(@RestClient JasparRegionDataFetcher getJasparDataAdapter) {
        this.jasparRegionDataFetcher = getJasparDataAdapter;
    }

    @Override
    public List<BlatSearchOptions> fetchDataFromBlatSource(PromoterRegionDTO tfRequest) {

        List<BlatSearchOptions> listaFinal = new ArrayList<>();
        Elements elements = getJasparResponse(tfRequest.promoterRegion());

        List<String> jasparProcessData = getProcessData(elements);

        jasparProcessData.forEach(blastSearchOptions ->
                listaFinal.add(buildBlastSearchOptions(blastSearchOptions))
        );


        listaFinal.sort(Comparator.comparingDouble(BlatSearchOptions::identity).reversed());

        return listaFinal.stream().filter(l -> l.identity() >= tfRequest.reliability()).toList();
    }

    @Override
    public List<TranscriptionFactor> fetchDataFromJasparSource(JasparRequest jasparRequest) {

        JasparRegionData jasparRegionData = jasparRegionDataFetcher.getRegionData(ConsultaJaspar.of(jasparRequest));

        List<JasparRegionData.JasparTranscriptionFactor> jasparRecords = jasparRegionData.getTranscriptionFactors();
        int maxScore = Collections.max(jasparRecords, Comparator.comparingInt(JasparRegionData.JasparTranscriptionFactor::score)).score();

        float mayora = maxScore * ((float) jasparRequest.reliability() / 100);

        return jasparRecords.stream()
                .filter(r -> r.score() >= mayora)
                .map(jasparRecord -> TranscriptionFactor.builder()
                        .name(jasparRecord.TFName())
                        .reliability(jasparRecord.score() * ((float) 100 / maxScore))
                        .source(TranscriptionFactorSource.JASPAR)
                        .sign("(" + jasparRequest.strand() + ")")
                        .build())
                .toList();
    }

    private static List<String> getProcessData(Elements elements) {
        return elements.get(0).childNodes().stream()
                .filter(TextNode.class::isInstance)
                .map(node -> ((TextNode) node).text().trim())
                .filter(text -> text.contains("YourSeq"))
                .toList();
    }

    private Elements getJasparResponse(String promoterRegion) {
        String tfBindBaseUrl = "https://genome.ucsc.edu/cgi-bin/hgBlat?type=BLAT%27s+guess&userSeq=" + promoterRegion;
        Elements elements = new Elements();

        try {
            Document doc = Jsoup.connect(tfBindBaseUrl).get();
            elements = doc.select("pre");
        } catch (IOException e) {
            log.error("Error to connect or read : {}", e.getMessage());
        }

        return elements;
    }

    private BlatSearchOptions buildBlastSearchOptions(String blastSearchOptions) {
        List<String> tfParts = Arrays.stream(blastSearchOptions.split(" ")).toList();
        return BlatSearchOptions.builder()
                .identity(Double.parseDouble(tfParts.get(5).replace("%", "")))
                .chromosome(tfParts.get(6))
                .strand(tfParts.get(7))
                .start(tfParts.get(8))
                .end(tfParts.get(9))
                .build();
    }
}
