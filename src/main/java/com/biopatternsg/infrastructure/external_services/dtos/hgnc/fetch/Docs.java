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
    @JsonProperty("date_name_changed") String dateNameChanged, // O Instant/LocalDate si quieres parsearlo
    @JsonProperty("mgd_id") List<String> mgdId,
    Integer orphanet, // Puede ser int si siempre está presente, o Integer para permitir null
    @JsonProperty("alias_name") List<String> aliasName,
    String location,
    @JsonProperty("uniprot_ids") List<String> uniprotIds,
    @JsonProperty("vega_id") String vegaId,
    @JsonProperty("refseq_accession") List<String> refseqAccession,
    List<String> ena,
    String gencc,
    @JsonProperty("date_approved_reserved") String dateApprovedReserved, // O Instant/LocalDate
    String name,
    @JsonProperty("rgd_id") List<String> rgdId,
    @JsonProperty("date_modified") String dateModified, // O Instant/LocalDate
    @JsonProperty("ucsc_id") String ucscId,
    @JsonProperty("ensembl_gene_id") String ensemblGeneId,
    @JsonProperty("hgnc_id") String hgncId
) {}
