package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.KpaCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TumorausbreitungCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TumorgradingCatalogue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static dev.pcvolkmer.onco.datamapper.mapper.MapperUtils.getPatientReference;

/**
 * Mapper class to load and map diagnosis data from database table 'dk_dnpm_kpa'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaDiagnosisDataMapper implements DataMapper<MtbDiagnosis> {

    private final KpaCatalogue kpaCatalogue;
    private final TumorausbreitungCatalogue tumorausbreitungCatalogue;
    private final TumorgradingCatalogue tumorgradingCatalogue;
    private final PropertyCatalogue propertyCatalogue;

    public KpaDiagnosisDataMapper(
            final KpaCatalogue kpaCatalogue,
            final TumorausbreitungCatalogue tumorausbreitungCatalogue,
            final TumorgradingCatalogue tumorgradingCatalogue,
            final PropertyCatalogue propertyCatalogue
    ) {
        this.kpaCatalogue = kpaCatalogue;
        this.tumorausbreitungCatalogue = tumorausbreitungCatalogue;
        this.tumorgradingCatalogue = tumorgradingCatalogue;
        this.propertyCatalogue = propertyCatalogue;
    }

    /**
     * Loads and maps a diagnosis using the kpa procedures database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded MtbDiagnosis file
     */
    @Override
    public MtbDiagnosis getById(int id) {
        var data = kpaCatalogue.getById(id);

        var builder = MtbDiagnosis.builder();
        builder
                .id(data.getString("id"))
                .patient(getPatientReference(data.getString("patient_id")))
                .code(
                        Coding.builder()
                                .code(data.getString("icd10"))
                                .system("http://fhir.de/CodeSystem/bfarm/icd-10-gm")
                                .display(propertyCatalogue.getByCodeAndVersion(data.getString("icd10"), data.getInteger("icd10_propcat_version")).getShortdesc())
                                .version(propertyCatalogue.getByCodeAndVersion(data.getString("icd10"), data.getInteger("icd10_propcat_version")).getVersionDescription())
                                .build()
                )
                .topography(Coding.builder().code(data.getString("icdo3localisation")).build())
                // Nicht in Onkostar erfasst
                //.germlineCodes()
                .guidelineTreatmentStatus(
                        getMtbDiagnosisGuidelineTreatmentStatusCoding(data.getString("leitlinienstatus"), data.getInteger("leitlinienstatus_propcat_version"))
                )
                .grading(getGrading(id))
                .staging(getStaging(id))
        ;
        return builder.build();
    }

    private MtbDiagnosisGuidelineTreatmentStatusCoding getMtbDiagnosisGuidelineTreatmentStatusCoding(final String code, final int version) {
        if (code == null || !Arrays.stream(MtbDiagnosisGuidelineTreatmentStatusCodingCode.values()).map(MtbDiagnosisGuidelineTreatmentStatusCodingCode::toValue).collect(Collectors.toSet()).contains(code)) {
            return null;
        }

        var resultBuilder = MtbDiagnosisGuidelineTreatmentStatusCoding.builder()
                .display(propertyCatalogue.getByCodeAndVersion(code, version).getShortdesc())
                .system("dnpm-dip/mtb/diagnosis/guideline-treatment-status");
        try {
            resultBuilder.code(MtbDiagnosisGuidelineTreatmentStatusCodingCode.forValue(code));
        } catch (IOException e) {
            throw new IllegalStateException("No valid code found");
        }

        return resultBuilder.build();
    }

    private Grading getGrading(final int id) {
        var all = tumorgradingCatalogue.getAllByParentId(id).stream()
                .map(resultSet -> {
                            var builder = TumorGrading.builder()
                                    .date(resultSet.getDate("zeitpunkt"));

                            if (null != resultSet.getString("tumorgrading") && !resultSet.getString("tumorgrading").isBlank()) {
                                var propertyEntry = propertyCatalogue
                                        .getByCodeAndVersion(resultSet.getString("tumorgrading"), resultSet.getInteger("tumorgrading_propcat_version"));
                                builder.codes(
                                        List.of(
                                                Coding.builder()
                                                        .code(resultSet.getString("tumorgrading"))
                                                        .system("https://www.basisdatensatz.de/feld/161/grading")
                                                        // TODO Annahme: "v1" ist Version 2025
                                                        .version(propertyEntry.getVersionDescription().equals("v1") ? "2025" : null)
                                                        .display(propertyEntry.getShortdesc())
                                                        .build()
                                        )
                                );
                                return builder.build();
                            } else if (null != resultSet.getString("whograd") && !resultSet.getString("whograd").isBlank()) {
                                var propertyEntry = propertyCatalogue
                                        .getByCodeAndVersion(resultSet.getString("whograd"), resultSet.getInteger("whograd_propcat_version"));
                                builder.codes(
                                        List.of(
                                                Coding.builder()
                                                        .code(resultSet.getString("whograd"))
                                                        .system("dnpm-dip/mtb/who-grading-cns-tumors")
                                                        .version(propertyEntry.getVersionDescription())
                                                        .display(propertyEntry.getShortdesc())
                                                        .build()
                                        )
                                );
                                return builder.build();
                            }

                            return null;
                        }
                ).filter(Objects::nonNull).collect(Collectors.toList());

        if (all.isEmpty()) {
            return null;
        }

        return Grading.builder().history(all).build();
    }

    private Staging getStaging(final int id) {
        var subMapper = new KpaTumorausbreitungDataMapper(tumorausbreitungCatalogue);

        var all = tumorausbreitungCatalogue.getAllByParentId(id).stream()
                .map(it -> subMapper.getById(it.getInteger("id")))
                .collect(Collectors.toList());
        if (all.isEmpty()) {
            return null;
        }

        return Staging.builder().history(all).build();
    }
}
