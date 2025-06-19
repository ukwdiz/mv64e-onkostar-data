package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.mv64e.mtb.Address;
import dev.pcvolkmer.mv64e.mtb.GenderCoding;
import dev.pcvolkmer.mv64e.mtb.GenderCodingCode;
import dev.pcvolkmer.mv64e.mtb.Patient;
import dev.pcvolkmer.onco.datamapper.datacatalogues.PatientCatalogue;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Mapper class to load and map diagnosis data from database table 'dk_dnpm_kpa'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class PatientDataMapper implements DataMapper<Patient> {

    private final JdbcTemplate jdbcTemplate;

    private PatientDataMapper(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Create instance of the mapper class
     *
     * @param jdbcTemplate The Spring JdbcTemplate to be used
     * @return The initialized mapper
     */
    public static PatientDataMapper create(final JdbcTemplate jdbcTemplate) {
        return new PatientDataMapper(jdbcTemplate);
    }

    /**
     * Loads and maps a patient using the patient database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded MtbDiagnosis file
     */
    @Override
    public Patient getById(int id) {
        var patientCatalogue = PatientCatalogue.create(this.jdbcTemplate);
        var patientData = patientCatalogue.getById(id);

        var builder = Patient.builder();
        try {
            builder
                    .id(patientData.getString("id"))
                    .gender(getGenderCoding(patientData))
                    .birthDate(mapDate(patientData.getDate("geburtsdatum")))
                    .dateOfDeath(mapDate(patientData.getDate("sterbedatum")))
                    .address(Address.builder().municipalityCode(getMunicipalityCode(patientData)).build())
            ;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return builder.build();
    }

    private GenderCoding getGenderCoding(ResultSet data) throws SQLException {
        var genderCodingBuilder = GenderCoding.builder();
        String geschlecht = data.getString("geschlecht");
        switch (geschlecht) {
            case "M":
                genderCodingBuilder.code(GenderCodingCode.MALE);
                break;
            case "F":
                genderCodingBuilder.code(GenderCodingCode.FEMALE);
                break;
            case "X":
                genderCodingBuilder.code(GenderCodingCode.OTHER);
                break;
            default:
                genderCodingBuilder.code(GenderCodingCode.UNKNOWN);
        }
        return genderCodingBuilder.build();
    }

    private String getMunicipalityCode(ResultSet data) throws SQLException {
        var gkz = data.getString("GKZ");
        if (gkz == null || gkz.trim().length() != 8) {
            throw new DataAccessException("Municipality code not found");
        }
        return gkz.substring(0, 5);
    }

}
