package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;

/**
 * Load raw result sets from database table 'dk_dnpm_kpa'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class Kpa {

    private final JdbcTemplate jdbcTemplate;

    private Kpa(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static Kpa create(JdbcTemplate jdbcTemplate) {
        return new Kpa(jdbcTemplate);
    }

    /**
     * Get procedure result set by procedure id
     * @param id The procedure id
     * @return The procedure id
     */
    public ResultSet getById(int id) {
        var result = this.jdbcTemplate.query(
                "SELECT * FROM dk_dnpm_kpa id = ?",
                (resultSet, i) -> resultSet,
                id);

        if (result.isEmpty()) {
            throw new DataAccessException("No record found for id: " + id);
        } else if (result.size() > 1) {
            throw new DataAccessException("Multiple records found for id: " + id);
        }

        return result.get(0);
    }

    /**
     * Get procedure database id by case id
     * @param caseId The case id
     * @return The procedure id
     */
    public int getProcedureIdByCaseId(String caseId) {
        var result = this.jdbcTemplate.query(
                "SELECT id FROM dk_dnpm_kpa WHERE fallnummermv = ?",
                (resultSet, i) -> resultSet.getInt(1),
                caseId);

        if (result.isEmpty()) {
            throw new DataAccessException("No record found for case: " + caseId);
        } else if (result.size() > 1) {
            throw new DataAccessException("Multiple records found for case: " + caseId);
        }

        return result.get(0);
    }

    /**
     * Get patient database id by case id
     * @param caseId The case id
     * @return The patients database id
     */
    public int getPatientIdByCaseId(String caseId) {
        var result = this.jdbcTemplate.query(
                "SELECT patient_id FROM dk_dnpm_kpa JOIN prozedur ON (prozedur.id = dk_dnpm_kpa.id) WHERE fallnummermv = ?",
                (resultSet, i) -> resultSet.getInt(1),
                caseId);

        if (result.isEmpty()) {
            throw new DataAccessException("No record found for case: " + caseId);
        } else if (result.size() > 1) {
            throw new DataAccessException("Multiple records found for case: " + caseId);
        }

        return result.get(0);
    }

}
