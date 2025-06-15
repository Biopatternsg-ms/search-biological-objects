package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import com.biopatternsg.domain.enums.TranscriptionFactorSource;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.out.external_repositories.TFBindRepository;
import com.biopatternsg.infrastructure.external_services.QueryTFBIND;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ApplicationScoped
public class TFBindAdapter implements TFBindRepository {

    private final QueryTFBIND queryTFBIND;

    public TFBindAdapter(QueryTFBIND queryTFBIND) {
        this.queryTFBIND = queryTFBIND;
    }

    @Override
    public List<TranscriptionFactor> fetchDataFromTFBindSource(PromoterRegionRequest tfRequest) {

        String query = queryTFBIND.getByPromoterRegion(tfRequest);

        List<String> unprocessTranscriptionFactors = Jsoup.parse(query).body().childNodes().stream()
                .filter(TextNode.class::isInstance)
                .filter(node -> !((TextNode) node).isBlank())
                .map(node -> ((TextNode) node).text().trim())
                .toList();

        return unprocessTranscriptionFactors.stream()
                .skip(2)
                .map(this::buildTF)
                .filter(tf -> (tf.reliability() * 100) >= tfRequest.reliability())
                .toList();
    }

    private TranscriptionFactor buildTF(String tfData) {
        List<String> tfParts = Arrays.stream(tfData.split(" ")).toList();

        return TranscriptionFactor.builder()
                .name(getTFName(tfParts.get(1)))
                .reliability(Float.parseFloat(tfParts.get(2)))
                .number(Integer.parseInt(tfParts.get(3)))
                .sign(tfParts.get(4))
                .chain(tfParts.get(5) + " " + tfParts.get(6))
                .source(TranscriptionFactorSource.TFBIND)
                .build();
    }

    private String getTFName(String nameUnprocess) {
        int startIndex = nameUnprocess.indexOf("$");
        int endIndex = nameUnprocess.indexOf("_");

        return nameUnprocess.substring(startIndex + 1, endIndex);
    }
}
