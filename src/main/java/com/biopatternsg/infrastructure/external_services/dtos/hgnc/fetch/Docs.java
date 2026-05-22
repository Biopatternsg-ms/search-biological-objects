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
package com.biopatternsg.infrastructure.external_services.dtos.hgnc.fetch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Docs(
    String symbol,
    String cosmic,
    @JsonProperty("locus_type") String locusType,
    @JsonProperty("pubmed_id") List<Integer> pubmedId,
    @JsonProperty("mane_select") List<String> maneSelect,
    @JsonProperty("locus_group") String locusGroup,
    List<String> lsdb,
    @JsonProperty("omim_id") List<String> omimId,
    String uuid,
    String agr,
    @JsonProperty("entrez_id") String entrezId,
    @JsonProperty("alias_symbol") List<String> aliasSymbol,
    String status,
    @JsonProperty("date_name_changed") String dateNameChanged,
    @JsonProperty("mgd_id") List<String> mgdId,
    Integer orphanet,
    @JsonProperty("alias_name") List<String> aliasName,
    String location,
    @JsonProperty("uniprot_ids") List<String> uniprotIds,
    @JsonProperty("vega_id") String vegaId,
    @JsonProperty("refseq_accession") List<String> refseqAccession,
    List<String> ena,
    String gencc,
    @JsonProperty("date_approved_reserved") String dateApprovedReserved,
    String name,
    @JsonProperty("rgd_id") List<String> rgdId,
    @JsonProperty("date_modified") String dateModified,
    @JsonProperty("ucsc_id") String ucscId,
    @JsonProperty("ensembl_gene_id") String ensemblGeneId,
    @JsonProperty("hgnc_id") String hgncId
) {}
