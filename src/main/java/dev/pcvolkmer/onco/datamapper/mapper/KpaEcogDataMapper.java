package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.EcogCoding;
import dev.pcvolkmer.mv64e.mtb.EcogCodingCode;
import dev.pcvolkmer.mv64e.mtb.PerformanceStatus;
import dev.pcvolkmer.mv64e.mtb.Reference;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.EcogCatalogue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to load and map prozedur data from database table 'dk_dnpm_uf_ecog'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaEcogDataMapper extends AbstractSubformDataMapper<PerformanceStatus> {

    public KpaEcogDataMapper(final EcogCatalogue catalogue) {
        super(catalogue);
    }

    /**
     * Loads and maps Prozedur related by database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded data set
     */
    @Override
    public PerformanceStatus getById(final int id) {
        var data = catalogue.getById(id);
        return this.map(data);
    }

    @Override
    public List<PerformanceStatus> getByParentId(final int parentId) {
        return catalogue.getAllByParentId(parentId)
                .stream()
                .map(this::map)
                .sorted(Comparator.comparing(PerformanceStatus::getEffectiveDate))
                .collect(Collectors.toList());
    }

    @Override
    protected PerformanceStatus map(final ResultSet resultSet) {
        var builder = PerformanceStatus.builder();
        builder
                .id(resultSet.getProcedureId().toString())
                .patient(
                        Reference.builder()
                                .id(resultSet.getString("patient_id"))
                                // Use "Patient" since Onkostar only provides patient data
                                .type("Patient")
                                .build()
                )
                .effectiveDate(resultSet.getDate("datum"))
                .value(getEcogCoding(resultSet.getString("ecog")))
        ;

        return builder.build();
    }

    private EcogCoding getEcogCoding(final String value) {
        if (value == null || !Arrays.stream(EcogCodingCode.values()).map(EcogCodingCode::toValue).collect(Collectors.toSet()).contains(value)) {
            return null;
        }

        var resultBuilder = EcogCoding.builder()
                .system("ECOG-Performance-Status");

        try {
            resultBuilder.code(EcogCodingCode.forValue(value));
            resultBuilder.display(String.format("ECOG %s", value));
        } catch (IOException e) {
            throw new IllegalStateException("No valid code found");
        }

        return resultBuilder.build();
    }

}
