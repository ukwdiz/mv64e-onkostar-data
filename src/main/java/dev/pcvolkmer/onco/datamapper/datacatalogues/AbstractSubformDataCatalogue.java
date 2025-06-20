package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.util.List;

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
    public List<ResultSet> getAllByMainId(int id) {
        var result = this.jdbcTemplate.query(
                String.format("SELECT * FROM %s JOIN prozedur ON (prozedur.id = %s.id) WHERE hauptprozedur = ?", getTableName(), getTableName()),
                (resultSet, i) -> resultSet,
                id);

        if (result.isEmpty()) {
            throw new DataAccessException("No record found for id: " + id);
        }

        return result;
    }

}
