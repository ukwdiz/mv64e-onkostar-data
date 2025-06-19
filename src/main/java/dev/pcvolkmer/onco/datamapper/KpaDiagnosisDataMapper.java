package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.mv64e.mtb.Coding;
import dev.pcvolkmer.mv64e.mtb.MtbDiagnosis;
import dev.pcvolkmer.onco.datamapper.datacatalogues.KpaCatalogue;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

/**
 * Mapper class to load and map diagnosis data from database table 'dk_dnpm_kpa'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaDiagnosisDataMapper implements DataMapper<MtbDiagnosis> {

    private final JdbcTemplate jdbcTemplate;

    private KpaDiagnosisDataMapper(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Create instance of the mapper class
     *
     * @param jdbcTemplate The Spring JdbcTemplate to be used
     * @return The initialized mapper
     */
    public static KpaDiagnosisDataMapper create(final JdbcTemplate jdbcTemplate) {
        return new KpaDiagnosisDataMapper(jdbcTemplate);
    }

    /**
     * Loads and maps a diagnosis using the kpa procedures database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded MtbDiagnosis file
     */
    @Override
    public MtbDiagnosis getById(int id) {
        var kpa = KpaCatalogue.create(this.jdbcTemplate);
        var data = kpa.getById(id);

        var builder =  MtbDiagnosis.builder();
        try {
            builder
                    .id(data.getString("id"))
                    .code(
                            Coding.builder()
                                    .code(data.getString("icd10"))
                                    .build()
                    );
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return builder.build();
    }

}
