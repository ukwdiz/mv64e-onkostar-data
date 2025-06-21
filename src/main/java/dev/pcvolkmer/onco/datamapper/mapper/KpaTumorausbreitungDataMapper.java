package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TumorausbreitungCatalogue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to load and map prozedur data from database table 'dk_dnpm_uf_tumorausbreitung'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaTumorausbreitungDataMapper implements SubformDataMapper<TumorStaging> {

    private final TumorausbreitungCatalogue catalogue;

    public KpaTumorausbreitungDataMapper(final TumorausbreitungCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    /**
     * Loads and maps Prozedur related by database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded data set
     */
    @Override
    public TumorStaging getById(final int id) {
        var data = catalogue.getById(id);
        return this.map(data);
    }

    @Override
    public List<TumorStaging> getByParentId(final int parentId) {
        return catalogue.getAllByParentId(parentId)
                .stream()
                .map(this::map)
                .sorted(Comparator.comparing(TumorStaging::getDate))
                .collect(Collectors.toList());
    }

    private TumorStaging map(final ResultSet resultSet) {
        var diseases = catalogue.getDiseases(resultSet.getProcedureId());

        if (diseases.size() != 1) {
            throw new IllegalStateException(String.format("No unique disease for procedure %s", resultSet.getProcedureId()));
        }

        var builder = TumorStaging.builder();
        builder
                .date(resultSet.getDate("zeitpunkt"))
                .method(getTumorStagingMethodCoding(resultSet.getString("typ")))
                .otherClassifications(List.of(Coding.builder().code(resultSet.getString("wert")).build()))
                .tnmClassification(getTnmClassification(resultSet))
        ;


        return builder.build();
    }

    private TumorStagingMethodCoding getTumorStagingMethodCoding(final String value) {
        if (value == null || !Arrays.stream(TumorStagingMethodCodingCode.values()).map(TumorStagingMethodCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = TumorStagingMethodCoding.builder();
        try {
            resultBuilder.code(TumorStagingMethodCodingCode.forValue(value));
        } catch (IOException e) {
            throw new IllegalStateException("No valid code found");
        }

        return resultBuilder.build();
    }

    private TnmClassification getTnmClassification(final ResultSet resultSet) {
        var tnpmClassificationBuilder = TnmClassification.builder();

        var hasContent = false;

        var tnmt = resultSet.getString("tnmt");
        if (tnmt != null && !tnmt.isBlank()) {
            tnpmClassificationBuilder.tumor(
                    Coding.builder().code(String.format("%s%s", resultSet.getString("tnmtprefix"), tnmt)).build()
            );
            hasContent = true;
        }

        var tnmn = resultSet.getString("tnmn");
        if (tnmn != null && !tnmn.isBlank()) {
            tnpmClassificationBuilder.nodes(
                    Coding.builder().code(String.format("%s%s", resultSet.getString("tnmnprefix"), tnmn)).build()
            );
            hasContent = true;
        }

        var tnmm = resultSet.getString("tnmm");
        if (tnmm != null && !tnmm.isBlank()) {
            tnpmClassificationBuilder.metastasis(
                    Coding.builder().code(String.format("%s%s", resultSet.getString("tnmmprefix"), tnmm)).build()
            );
            hasContent = true;
        }

        if (hasContent) {
            return tnpmClassificationBuilder.build();
        }

        return null;
    }

}
