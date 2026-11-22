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
    public List<BlatSearchOptionsResponse> fetchDataFromBlatSource(int reliability, String promoterRegion) {

        PromoterRegionRequest tfRequest = new PromoterRegionRequest(reliability, promoterRegion);
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
                .filter(l -> l.identity() >= reliability)
                .sorted(Comparator.comparingDouble(BlatSearchOptionsResponse::identity).reversed())
                .toList();
    }
}
