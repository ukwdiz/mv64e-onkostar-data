package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;

/**
 * Common implementations for all data catalogues
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public abstract class AbstractDataCatalogue implements DataCatalogue {

    protected final JdbcTemplate jdbcTemplate;

    protected AbstractDataCatalogue(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected abstract String getTableName();

    /**
     * Get procedure result set by procedure id
     *
     * @param id The procedure id
     * @return The procedure id
     */
    @Override
    public ResultSet getById(int id) {
        var result = this.jdbcTemplate.query(
                String.format("SELECT * FROM %s JOIN prozedur ON (prozedur.id = %s.id) WHERE id = ?", getTableName(), getTableName()),
                (resultSet, i) -> resultSet,
                id);

        if (result.isEmpty()) {
            throw new DataAccessException("No record found for id: " + id);
        } else if (result.size() > 1) {
            throw new DataAccessException("Multiple records found for id: " + id);
        }

        return result.get(0);
    }

}
