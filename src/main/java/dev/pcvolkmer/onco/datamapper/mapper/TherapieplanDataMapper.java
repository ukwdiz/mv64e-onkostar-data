package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.EinzelempfehlungCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TherapieplanCatalogue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dev.pcvolkmer.onco.datamapper.mapper.MapperUtils.getPatientReference;

/**
 * Mapper class to load and map patient data from database table 'dk_dnpm_therapieplan'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class TherapieplanDataMapper implements DataMapper<MtbCarePlan> {

    private final TherapieplanCatalogue therapieplanCatalogue;
    private final PropertyCatalogue propertyCatalogue;

    private final EinzelempfehlungProzedurDataMapper einzelempfehlungProzedurDataMapper;
    private final EinzelempfehlungWirkstoffDataMapper einzelempfehlungWirkstoffDataMapper;

    public TherapieplanDataMapper(
            final TherapieplanCatalogue therapieplanCatalogue,
            final EinzelempfehlungCatalogue einzelempfehlungCatalogue,
            final PropertyCatalogue propertyCatalogue
    ) {
        this.therapieplanCatalogue = therapieplanCatalogue;
        this.propertyCatalogue = propertyCatalogue;

        this.einzelempfehlungProzedurDataMapper = new EinzelempfehlungProzedurDataMapper(einzelempfehlungCatalogue);
        this.einzelempfehlungWirkstoffDataMapper = new EinzelempfehlungWirkstoffDataMapper(einzelempfehlungCatalogue);
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
                .patient(getPatientReference(therapieplanData.getString("patient_id")))
                .issuedOn(therapieplanData.getDate("datum"))
                .medicationRecommendations(einzelempfehlungWirkstoffDataMapper.getByParentId(id))
                .procedureRecommendations(einzelempfehlungProzedurDataMapper.getByParentId(id))
        ;

        if (therapieplanData.getString("protokollauszug") != null) {
            // TODO see https://github.com/dnpm-dip/mtb-model/issues/8
            builder.notes(List.of(therapieplanData.getString("protokollauszug")));
        }

        if (
                null != therapieplanData.getString("status_begruendung")
                        && therapieplanData.getString("status_begruendung").equals(MtbCarePlanRecommendationsMissingReasonCodingCode.NO_TARGET.toValue())
        ) {
            builder.recommendationsMissingReason(
                    MtbCarePlanRecommendationsMissingReasonCoding.builder()
                            .code(MtbCarePlanRecommendationsMissingReasonCodingCode.NO_TARGET)
                            .build()
            );
        } else {
            builder.noSequencingPerformedReason(
                    getCarePlanNoSequencingPerformedReasonCoding(therapieplanData.getString("status_begruendung"))
            );
        }

        return builder.build();
    }

    private CarePlanNoSequencingPerformedReasonCoding getCarePlanNoSequencingPerformedReasonCoding(String value) {
        if (value == null || !Arrays.stream(NoSequencingPerformedReasonCode.values()).map(NoSequencingPerformedReasonCode::toValue).collect(Collectors.toSet()).contains(value)) {
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

}
