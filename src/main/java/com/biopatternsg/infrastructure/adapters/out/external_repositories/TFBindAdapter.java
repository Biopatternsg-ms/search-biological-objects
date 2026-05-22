/*
 * Copyright © 2026 biopatternsg (biopatternsg@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.biopatternsg.infrastructure.adapters.out.external_repositories;

import com.biopatternsg.domain.enums.TranscriptionFactorSource;
import com.biopatternsg.domain.models.TranscriptionFactor;
import com.biopatternsg.domain.port.out.external_repositories.TFBindRepository;
import com.biopatternsg.infrastructure.dtos.PromoterRegionRequest;
import com.biopatternsg.infrastructure.external_services.QueryTFBIND;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.TextNode;

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
