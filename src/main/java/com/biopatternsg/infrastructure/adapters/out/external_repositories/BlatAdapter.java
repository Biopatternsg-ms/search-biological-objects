package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.models.external_entities.BlatSearchOptionsResponse;
import com.biopatternsg.domain.port.out.external_repositories.BlatRepository;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import com.biopatternsg.infrastructure.external_services.QueryBlat;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.Comparator;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class BlatAdapter implements BlatRepository {

    private final QueryBlat queryBlat;

    @Override
    public List<BlatSearchOptionsResponse> fetchDataFromBlatSource(PromoterRegionRequest tfRequest) {

        String byPromoterJasparRegion = queryBlat.getByPromoterJasparRegion(tfRequest);
        Elements elements = Jsoup.parse(byPromoterJasparRegion).select("pre");

        List<String> blatProcessData = elements
                .get(0).childNodes().stream()
                .filter(TextNode.class::isInstance)
                .map(node -> ((TextNode) node).text().trim())
                .filter(text -> text.contains("YourSeq"))
                .toList();

        return blatProcessData.stream()
                .map(BlatSearchOptionsResponse::of)
                .filter(l -> l.identity() >= tfRequest.reliability())
                .sorted(Comparator.comparingDouble(BlatSearchOptionsResponse::identity).reversed())
                .toList();
    }
}
