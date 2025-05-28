package com.biopatternsg.infrastructure.adapters.out;

import com.biopatternsg.infrastructure.dtos.PromoterRegionDTO;
import com.biopatternsg.domain.enums.TranscriptionFactorSource;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.TranscriptionFactorTFBindDataProviderPort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ApplicationScoped
public class TranscriptionFactorTFBindDataProviderAdapter implements TranscriptionFactorTFBindDataProviderPort {

    @Override
    public List<TranscriptionFactor> fetchDataFromTFBindSource(PromoterRegionDTO tfRequest) {

        List<TranscriptionFactor> tfList = new ArrayList<>();
        Element body = getTFBindResponseBody(tfRequest.promoterRegion());

        List<String> tfProcessData = getTFProcessData(body);

        tfProcessData.subList(2, tfProcessData.size())
                .forEach(tfData-> tfList.add(buildTF(tfData)));

        return tfList.stream()
                .filter(tf -> (tf.reliability() * 100) >= tfRequest.reliability())
                .toList();
    }

    private static List<String> getTFProcessData(Element body) {
        return body.childNodes().stream()
                .filter(TextNode.class::isInstance)
                .filter(node -> !((TextNode) node).isBlank())
                .map(node -> ((TextNode) node).text().trim())
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

    private Element getTFBindResponseBody(String promoterRegion){
        String tfBindBaseUrl = "https://tfbind.hgc.jp/cgi-bin/calculate.cgi";
        String param = "seq";
        String value = "> COMMENTS\r\n" + promoterRegion;
        Element body = new Element("body");

        try {
            String tfBindUrl = tfBindBaseUrl + "?" + URLEncoder.encode(param, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
            Document doc = Jsoup.connect(tfBindUrl).get();
            body = doc.body();
        } catch (IOException e) {
            log.error("Error to connect or read : {}", e.getMessage());
        }

        return body;
    }
}
