/*
 * This file is part of mv64e-onkostar-data
 *
 * Copyright (C) 2025  Paul-Christian Volkmer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.EinzelempfehlungCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.RebiopsieCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.ReevaluationCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TherapieplanCatalogue;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to load and map patient data from database table 'dk_dnpm_therapieplan'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class TherapieplanDataMapper implements DataMapper<MtbCarePlan> {

  private final TherapieplanCatalogue therapieplanCatalogue;
  private final RebiopsieCatalogue rebiopsieCatalogue;
  private final ReevaluationCatalogue reevaluationCatalogue;
  private final PropertyCatalogue propertyCatalogue;

  private final EinzelempfehlungProzedurDataMapper einzelempfehlungProzedurDataMapper;
  private final EinzelempfehlungWirkstoffDataMapper einzelempfehlungWirkstoffDataMapper;
  private final EinzelempfehlungStudieDataMapper einzelempfehlungStudieDataMapper;

  public TherapieplanDataMapper(
      final TherapieplanCatalogue therapieplanCatalogue,
      final RebiopsieCatalogue rebiopsieCatalogue,
      final ReevaluationCatalogue reevaluationCatalogue,
      final EinzelempfehlungCatalogue einzelempfehlungCatalogue,
      final PropertyCatalogue propertyCatalogue) {
    this.therapieplanCatalogue = therapieplanCatalogue;
    this.rebiopsieCatalogue = rebiopsieCatalogue;
    this.reevaluationCatalogue = reevaluationCatalogue;
    this.propertyCatalogue = propertyCatalogue;

    this.einzelempfehlungProzedurDataMapper =
        new EinzelempfehlungProzedurDataMapper(einzelempfehlungCatalogue);
    this.einzelempfehlungWirkstoffDataMapper =
        new EinzelempfehlungWirkstoffDataMapper(einzelempfehlungCatalogue, propertyCatalogue);
    this.einzelempfehlungStudieDataMapper =
        new EinzelempfehlungStudieDataMapper(einzelempfehlungCatalogue);
  }

  /**
   * Loads and maps a ca plan using the database id
   *
   * @param id The database id of the procedure data set
   * @return The loaded Patient data
   */
  @Override
  public MtbCarePlan getById(int id) {
    var therapieplanData = therapieplanCatalogue.getById(id);

    var builder = MtbCarePlan.builder();
    builder
        .id(therapieplanData.getString("id"))
        .patient(therapieplanData.getPatientReference())
        .issuedOn(therapieplanData.getDate("datum"))
        .histologyReevaluationRequests(getHistologyReevaluationRequests(id))
        .rebiopsyRequests(
            getRebiopsyRequest(
                id,
                Reference.builder()
                    .id(therapieplanData.getString("refdnpmklinikanamnese"))
                    .type("MTBDiagnosis")
                    .build()));

    if (therapieplanData.isTrue("mit_einzelempfehlung")) {
      builder.medicationRecommendations(einzelempfehlungWirkstoffDataMapper.getByParentId(id));
      builder.procedureRecommendations(einzelempfehlungProzedurDataMapper.getByParentId(id));
      builder.studyEnrollmentRecommendations(einzelempfehlungStudieDataMapper.getByParentId(id));
    }

    // Formularfeld "protokollauszug"
    if (therapieplanData.getString("protokollauszug") != null) {
      // TODO see https://github.com/dnpm-dip/mtb-model/issues/8
      builder.notes(List.of(therapieplanData.getString("protokollauszug")));
    }

    // Formularfeld "status_begruendung"
    if (null != therapieplanData.getString("status_begruendung")
        && therapieplanData
            .getString("status_begruendung")
            .equals(MtbCarePlanRecommendationsMissingReasonCodingCode.NO_TARGET.toValue())) {
      builder.recommendationsMissingReason(
          MtbCarePlanRecommendationsMissingReasonCoding.builder()
              .code(MtbCarePlanRecommendationsMissingReasonCodingCode.NO_TARGET)
              .build());
    } else {
      builder.noSequencingPerformedReason(
          getCarePlanNoSequencingPerformedReasonCoding(
              therapieplanData.getString("status_begruendung")));
    }

    // Humangenetische Beratung
    if (therapieplanData.isTrue("humangen_beratung")) {
      builder.geneticCounselingRecommendation(
          GeneticCounselingRecommendation.builder()
              .id(therapieplanData.getString("id"))
              .patient(therapieplanData.getPatientReference())
              .issuedOn(therapieplanData.getDate("datum_tk_humangenber"))
              .reason(
                  getGeneticCounselingRecommendationReasonCoding(
                      therapieplanData.getString("humangen_ber_grund"),
                      therapieplanData.getInteger("humangen_ber_grund_propcat_version")))
              .build());
    }

    return builder.build();
  }

  private CarePlanNoSequencingPerformedReasonCoding getCarePlanNoSequencingPerformedReasonCoding(
      String value) {
    if (value == null
        || !Arrays.stream(NoSequencingPerformedReasonCode.values())
            .map(NoSequencingPerformedReasonCode::toValue)
            .collect(Collectors.toSet())
            .contains(value)) {
      return null;
    }

    var resultBuilder = CarePlanNoSequencingPerformedReasonCoding.builder();
    try {
      resultBuilder.code(NoSequencingPerformedReasonCode.forValue(value));
    } catch (IOException e) {
      return null;
    }

    return resultBuilder.build();
  }

  private GeneticCounselingRecommendationReasonCoding
      getGeneticCounselingRecommendationReasonCoding(String value, int version) {
    if (value == null
        || !Arrays.stream(GeneticCounselingRecommendationReasonCodingCode.values())
            .map(GeneticCounselingRecommendationReasonCodingCode::toValue)
            .collect(Collectors.toSet())
            .contains(value)) {
      return null;
    }

    var resultBuilder =
        GeneticCounselingRecommendationReasonCoding.builder()
            .system("dnpm-dip/mtb/recommendation/genetic-counseling/reason");
    try {
      resultBuilder.code(GeneticCounselingRecommendationReasonCodingCode.forValue(value));
      resultBuilder.display(propertyCatalogue.getByCodeAndVersion(value, version).getShortdesc());
    } catch (IOException e) {
      return null;
    }

    return resultBuilder.build();
  }

  private List<RebiopsyRequest> getRebiopsyRequest(int parentId, Reference diagnosisReference) {
    return this.rebiopsieCatalogue.getAllByParentId(parentId).stream()
        .map(
            resultSet ->
                RebiopsyRequest.builder()
                    .id(resultSet.getString("id"))
                    .patient(resultSet.getPatientReference())
                    .issuedOn(resultSet.getDate("datum"))
                    .tumorEntity(diagnosisReference)
                    .build())
        .collect(Collectors.toList());
  }

  private List<HistologyReevaluationRequest> getHistologyReevaluationRequests(int parentId) {
    return this.reevaluationCatalogue.getAllByParentId(parentId).stream()
        .map(
            resultSet ->
                HistologyReevaluationRequest.builder()
                    .id(resultSet.getString("id"))
                    .patient(resultSet.getPatientReference())
                    .issuedOn(resultSet.getDate("datum"))
                    .specimen(
                        Reference.builder().id(resultSet.getString("ref_molekulargenetik")).build())
                    .build())
        .collect(Collectors.toList());
  }
}
