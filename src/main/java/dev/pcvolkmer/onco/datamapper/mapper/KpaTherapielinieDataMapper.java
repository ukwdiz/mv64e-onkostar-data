package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TherapielinieCatalogue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to load and map prozedur data from database table 'dk_dnpm_therapielinie'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaTherapielinieDataMapper implements SubformDataMapper<MtbSystemicTherapy> {

    private final TherapielinieCatalogue catalogue;

    public KpaTherapielinieDataMapper(final TherapielinieCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    /**
     * Loads and maps Prozedur related by database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded MtbDiagnosis file
     */
    @Override
    public MtbSystemicTherapy getById(final int id) {
        var data = catalogue.getById(id);
        return this.map(data);
    }

    @Override
    public List<MtbSystemicTherapy> getByParentId(final int parentId) {
        return catalogue.getAllByParentId(parentId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private MtbSystemicTherapy map(final ResultSet resultSet) {
        var diseases = catalogue.getDiseases(resultSet.getProcedureId());

        if (diseases.size() != 1) {
            throw new IllegalStateException(String.format("No unique disease for procedure %s", resultSet.getProcedureId()));
        }

        var builder = MtbSystemicTherapy.builder();
        builder
                .id(resultSet.getString("id"))
                .patient(Reference.builder().id(resultSet.getString("patient_id")).build())
                .basedOn(Reference.builder().id(diseases.get(0).getDiseaseId().toString()).build())
                .recordedOn(resultSet.getDate("erfassungsdatum"))
                .therapyLine(resultSet.getLong("therapielinie"))
                .intent(getMtbTherapyIntentCoding(resultSet.getString("intention")))
                .status(getTherapyStatusCoding(resultSet.getString("status")))
                .statusReason(getMtbTherapyStatusReasonCoding(resultSet.getString("statusgrund")))
                .period(PeriodDate.builder().start(resultSet.getDate("beginn")).end(resultSet.getDate("ende")).build())
        /* TODO JSON deserialisation */
        //.medication()

        /* TODO Yet missing form fields */
        //.category(getMtbSystemicTherapyCategoryCoding())
        //.dosage(getMtbSystemicTherapyDosageDensityCoding())
        //.recommendationFulfillmentStatus(getMtbSystemicTherapyRecommendationFulfillmentStatusCoding()
        ;
        return builder.build();
    }

    private MtbTherapyIntentCoding getMtbTherapyIntentCoding(String value) {
        if (value == null || !Arrays.stream(MtbTherapyIntentCodingCode.values()).map(MtbTherapyIntentCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = MtbTherapyIntentCoding.builder();

        switch (value) {
            case "X":
                resultBuilder.code(MtbTherapyIntentCodingCode.X);
                break;
            case "K":
                resultBuilder.code(MtbTherapyIntentCodingCode.K);
                break;
            case "P":
                resultBuilder.code(MtbTherapyIntentCodingCode.P);
                break;
            case "S":
                resultBuilder.code(MtbTherapyIntentCodingCode.S);
                break;
        }

        return resultBuilder.build();
    }

    private TherapyStatusCoding getTherapyStatusCoding(String value) {
        if (value == null || !Arrays.stream(TherapyStatusCodingCode.values()).map(TherapyStatusCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = TherapyStatusCoding.builder();

        switch (value) {
            case "not-done":
                resultBuilder.code(TherapyStatusCodingCode.NOT_DONE);
                break;
            case "on-going":
                resultBuilder.code(TherapyStatusCodingCode.ON_GOING);
                break;
            case "stopped":
                resultBuilder.code(TherapyStatusCodingCode.STOPPED);
                break;
            case "completed":
                resultBuilder.code(TherapyStatusCodingCode.COMPLETED);
                break;
        }

        return resultBuilder.build();
    }

    private MtbTherapyStatusReasonCoding getMtbTherapyStatusReasonCoding(String value) {
        if (value == null || !Arrays.stream(MtbTherapyStatusReasonCodingCode.values()).map(MtbTherapyStatusReasonCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = MtbTherapyStatusReasonCoding.builder();
        try {
            resultBuilder.code(MtbTherapyStatusReasonCodingCode.forValue(value));
        } catch (IOException e) {
            return null;
        }

        return resultBuilder.build();
    }

    private MtbSystemicTherapyRecommendationFulfillmentStatusCoding getMtbSystemicTherapyRecommendationFulfillmentStatusCoding(String value) {
        if (value == null || !Arrays.stream(MtbSystemicTherapyRecommendationFulfillmentStatusCodingCode.values()).map(MtbSystemicTherapyRecommendationFulfillmentStatusCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = MtbSystemicTherapyRecommendationFulfillmentStatusCoding.builder();
        try {
            resultBuilder.code(MtbSystemicTherapyRecommendationFulfillmentStatusCodingCode.forValue(value));
        } catch (IOException e) {
            return null;
        }

        return resultBuilder.build();
    }

    private MtbSystemicTherapyCategoryCoding getMtbSystemicTherapyCategoryCoding(String value) {
        if (value == null || !Arrays.stream(MtbSystemicTherapyCategoryCodingCode.values()).map(MtbSystemicTherapyCategoryCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = MtbSystemicTherapyCategoryCoding.builder();
        try {
            resultBuilder.code(MtbSystemicTherapyCategoryCodingCode.forValue(value));
        } catch (IOException e) {
            return null;
        }

        return resultBuilder.build();
    }

    private MtbSystemicTherapyDosageDensityCoding getMtbSystemicTherapyDosageDensityCoding(String value) {
        if (value == null || !Arrays.stream(MtbSystemicTherapyDosageDensityCodingCode.values()).map(MtbSystemicTherapyDosageDensityCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = MtbSystemicTherapyDosageDensityCoding.builder();
        try {
            resultBuilder.code(MtbSystemicTherapyDosageDensityCodingCode.forValue(value));
        } catch (IOException e) {
            return null;
        }

        return resultBuilder.build();
    }

}
