package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_kpa'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaCatalogue extends AbstractDataCatalogue {

    private KpaCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_kpa";
    }

    public static KpaCatalogue create(JdbcTemplate jdbcTemplate) {
        return new KpaCatalogue(jdbcTemplate);
    }

    /**
     * Get procedure database id by case id
     *
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
     * Get latest procedure database id by patient id and tumor id
     *
     * @param patientId The patients id (not database id)
     * @param tumorId The tumor identifier
     * @return The procedure id
     */
    public int getLatestProcedureIdByPatientIdAndTumor(String patientId, int tumorId) {
        var sql = "SELECT prozedur.id FROM dk_dnpm_kpa " +
                "    JOIN prozedur ON (prozedur.id = dk_dnpm_kpa.id) " +
                "    JOIN erkrankung_prozedur ON (erkrankung_prozedur.prozedur_id = prozedur.id) " +
                "    JOIN erkrankung ON (erkrankung_prozedur.erkrankung_id = erkrankung.id) " +
                "    JOIN patient ON (patient.id = prozedur.patient_id) " +
                "    WHERE patient.patienten_id = ? AND erkrankung.tumoridentifikator = ? " +
                "    ORDER BY dk_dnpm_kpa.anmeldedatummtb DESC " +
                "    LIMIT 1";

        var result = this.jdbcTemplate.query(
                sql,
                (resultSet, i) -> resultSet.getInt(1),
                patientId, tumorId);

        if (result.isEmpty()) {
            throw new DataAccessException(String.format("No record found for patient '%s' and tumor '%d'", patientId, tumorId));
        } else if (result.size() > 1) {
            // This should not happen due to LIMIT 1
            throw new DataAccessException(String.format("Multiple records found for patient '%s' and tumor '%d'", patientId, tumorId));
        }

        return result.get(0);
    }

    /**
     * Get patient database id by case id
     *
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
