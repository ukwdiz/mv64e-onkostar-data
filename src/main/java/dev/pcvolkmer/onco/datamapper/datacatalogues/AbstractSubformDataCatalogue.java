package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Common implementations for all data catalogues used in subforms
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public abstract class AbstractSubformDataCatalogue extends AbstractDataCatalogue implements DataCatalogue {

    protected AbstractSubformDataCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    protected abstract String getTableName();

    /**
     * Get procedure result sets by main procedure id
     *
     * @param id The procedure id
     * @return The procedure id
     */
    public List<Map<String, Object>> getAllByMainId(int id) {
        return this.jdbcTemplate.queryForList(
                String.format(
                        "SELECT * FROM %s JOIN prozedur ON (prozedur.id = %s.id) WHERE geloescht = 0 AND hauptprozedur_id = ?",
                        getTableName(),
                        getTableName()
                ),
                id);
    }

}
