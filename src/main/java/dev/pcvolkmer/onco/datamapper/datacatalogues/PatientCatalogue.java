package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;

/**
 * Load raw result sets from database table 'patient'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class PatientCatalogue {

    private final JdbcTemplate jdbcTemplate;

    private PatientCatalogue(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static PatientCatalogue create(JdbcTemplate jdbcTemplate) {
        return new PatientCatalogue(jdbcTemplate);
    }

    /**
     * Get patient result set by procedure id
     * @param id The procedure id
     * @return The procedure id
     */
    public ResultSet getById(int id) {
        var result = this.jdbcTemplate.query(
                "SELECT * FROM patient WHERE id = ?",
                (resultSet, i) -> resultSet,
                id);

        if (result.isEmpty()) {
            throw new DataAccessException("No patient record found for id: " + id);
        } else if (result.size() > 1) {
            throw new DataAccessException("Multiple patient records found for id: " + id);
        }

        return result.get(0);
    }

}
