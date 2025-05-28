package com.biopatternsg.infrastructure.adapters.out;

import com.biopatternsg.domain.models.BlatSearchOptions;
import com.biopatternsg.domain.port.BlatProviderPort;
import com.biopatternsg.infrastructure.dtos.PromoterRegionDTO;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@ApplicationScoped
public class BlatDataProviderAdapter implements BlatProviderPort {

    @Override
    public List<BlatSearchOptions> fetchDataFromBlatSource(PromoterRegionDTO tfRequest) {

        List<BlatSearchOptions> blatSearchOptions = new ArrayList<>();
        Elements elements = getJasparResponse(tfRequest.promoterRegion());

        List<String> blatProcessData = getProcessData(elements);

        blatProcessData.forEach(blatSearchOptionsString ->
                blatSearchOptions.add(BlatSearchOptions.of(blatSearchOptionsString))
        );

        return blatSearchOptions.stream()
                .filter(l -> l.identity() >= tfRequest.reliability())
                .sorted(Comparator.comparingDouble(BlatSearchOptions::identity).reversed())
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
}
