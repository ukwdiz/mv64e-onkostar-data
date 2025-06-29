package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.EinzelempfehlungCatalogue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static dev.pcvolkmer.onco.datamapper.mapper.MapperUtils.getPatientReference;

/**
 * Mapper class to load and map diagnosis data from database table 'dk_dnpm_einzelempfehlung'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class EinzelempfehlungProzedurDataMapper extends AbstractEinzelempfehlungDataMapper<ProcedureRecommendation> {

    public EinzelempfehlungProzedurDataMapper(EinzelempfehlungCatalogue einzelempfehlungCatalogue) {
        super(einzelempfehlungCatalogue);
    }

    @Override
    protected ProcedureRecommendation map(ResultSet resultSet) {
        return ProcedureRecommendation.builder()
                .id(resultSet.getString("id"))
                .patient(getPatientReference(resultSet.getString("patient_id")))
                // TODO Fix id?
                .reason(Reference.builder().id(resultSet.getString("id")).build())
                .issuedOn(resultSet.getDate("datum"))
                .priority(
                        getRecommendationPriorityCoding(
                                resultSet.getString("evidenzlevel"),
                                resultSet.getInteger("evidenzlevel_propcat_version")
                        )
                )
                .code(
                        getMtbProcedureRecommendationCategoryCoding(
                                resultSet.getString("art_der_therapie"),
                                resultSet.getInteger("art_der_therapie_propcat_version")
                        )
                )
                .levelOfEvidence(getLevelOfEvidence(resultSet))
                .build();
    }

    @Override
    public ProcedureRecommendation getById(int id) {
        return this.map(this.catalogue.getById(id));
    }

    @Override
    public List<ProcedureRecommendation> getByParentId(final int parentId) {
        return catalogue.getAllByParentId(parentId)
                .stream()
                // Filter Prozedurempfehlung (Weitere Empfehlungen)
                .filter(it -> it.getString("art_der_therapie") != null && !it.getString("art_der_therapie").isBlank())
                .map(this::map)
                .collect(Collectors.toList());
    }

    private RecommendationPriorityCoding getRecommendationPriorityCoding(String code, int version) {
        if (code == null || !Arrays.stream(RecommendationPriorityCodingCode.values()).map(RecommendationPriorityCodingCode::toValue).collect(Collectors.toSet()).contains(code)) {
            return null;
        }

        var resultBuilder = RecommendationPriorityCoding.builder()
                .system("dnpm-dip/recommendation/priority");

        try {
            resultBuilder
                    .code(RecommendationPriorityCodingCode.forValue(code))
                    .display(code);
        } catch (IOException e) {
            return null;
        }

        return resultBuilder.build();
    }

    private MtbProcedureRecommendationCategoryCoding getMtbProcedureRecommendationCategoryCoding(String code, int version) {
        if (code == null || !Arrays.stream(MtbProcedureRecommendationCategoryCodingCode.values()).map(MtbProcedureRecommendationCategoryCodingCode::toValue).collect(Collectors.toSet()).contains(code)) {
            return null;
        }

        var resultBuilder = MtbProcedureRecommendationCategoryCoding.builder()
                .system("dnpm-dip/mtb/recommendation/procedure/category");

        try {
            resultBuilder
                    .code(MtbProcedureRecommendationCategoryCodingCode.forValue(code))
                    .display(code);
        } catch (IOException e) {
            return null;
        }

        return resultBuilder.build();
    }

}
