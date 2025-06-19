package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.mv64e.mtb.Mtb;
import dev.pcvolkmer.onco.datamapper.datacatalogues.Kpa;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Mapper class to load and map Mtb files from database
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class MtbDataMapper implements DataMapper<Mtb> {

    private final JdbcTemplate jdbcTemplate;

    MtbDataMapper(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Create instance of the mapper class
     *
     * @param dataSource The datasource to be used
     * @return The initialized mapper
     */
    public static MtbDataMapper create(final DataSource dataSource) {
        return new MtbDataMapper(new JdbcTemplate(dataSource));
    }

    /**
     * Loads and maps a Mtb file using the root procedures database id
     *
     * @param id The database id of the root procedure data set
     * @return The loaded Mtb file
     */
    @Override
    public Mtb getById(int id) {
        return Mtb.builder().build();
    }

    /**
     * Loads and maps a Mtb file using the case id
     *
     * @param caseId The case id
     * @return The loaded Mtb file
     */
    public Mtb getByCaseId(String caseId) {
        var kpa = Kpa.create(this.jdbcTemplate);
        return this.getById(
                kpa.getProcedureIdByCaseId(caseId)
        );
    }
}
