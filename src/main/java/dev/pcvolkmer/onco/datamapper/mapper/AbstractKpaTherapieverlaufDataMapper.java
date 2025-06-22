package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.AbstractSubformDataCatalogue;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Abstract Mapper class for similar 'dk_dnpm_therapielinie' and 'dk_dnpm_uf_procedure'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public abstract class AbstractKpaTherapieverlaufDataMapper<T> extends AbstractSubformDataMapper<T> {

    private final PropertyCatalogue propertyCatalogue;

    AbstractKpaTherapieverlaufDataMapper(final AbstractSubformDataCatalogue catalogue, final PropertyCatalogue propertyCatalogue) {
        super(catalogue);
        this.propertyCatalogue = propertyCatalogue;
    }

    protected MtbTherapyIntentCoding getMtbTherapyIntentCoding(String value, int version) {
        if (value == null || !Arrays.stream(MtbTherapyIntentCodingCode.values()).map(MtbTherapyIntentCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = MtbTherapyIntentCoding.builder()
                .system("dnpm-dip/therapy/intent")
                .display(propertyCatalogue.getByCodeAndVersion(value, version).getShortdesc());

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

    protected TherapyStatusCoding getTherapyStatusCoding(String value, int version) {
        if (value == null || !Arrays.stream(TherapyStatusCodingCode.values()).map(TherapyStatusCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = TherapyStatusCoding.builder()
                .system("dnpm-dip/therapy/status")
                .display(propertyCatalogue.getByCodeAndVersion(value, version).getShortdesc());

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

    protected MtbTherapyStatusReasonCoding getMtbTherapyStatusReasonCoding(String value, int version) {
        if (value == null || !Arrays.stream(MtbTherapyStatusReasonCodingCode.values()).map(MtbTherapyStatusReasonCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = MtbTherapyStatusReasonCoding.builder()
                .system("dnpm-dip/therapy/status-reason")
                .display(propertyCatalogue.getByCodeAndVersion(value, version).getShortdesc());

        try {
            resultBuilder.code(MtbTherapyStatusReasonCodingCode.forValue(value));
        } catch (IOException e) {
            return null;
        }

        return resultBuilder.build();
    }

    protected MtbSystemicTherapyRecommendationFulfillmentStatusCoding getMtbSystemicTherapyRecommendationFulfillmentStatusCoding(String value, int version) {
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

    protected MtbSystemicTherapyCategoryCoding getMtbSystemicTherapyCategoryCoding(String value, int version) {
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

    protected MtbSystemicTherapyDosageDensityCoding getMtbSystemicTherapyDosageDensityCoding(String value, int version) {
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

    protected OncoProcedureCoding getOncoProcedureCoding(String value, int version) {
        if (value == null || !Arrays.stream(OncoProcedureCodingCode.values()).map(OncoProcedureCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = OncoProcedureCoding.builder()
                .system("dnpm-dip/therapy/type")
                .display(propertyCatalogue.getByCodeAndVersion(value, version).getShortdesc());
        
        try {
            resultBuilder.code(OncoProcedureCodingCode.forValue(value));
        } catch (IOException e) {
            throw new IllegalStateException("No valid code found");
        }

        return resultBuilder.build();
    }

}
