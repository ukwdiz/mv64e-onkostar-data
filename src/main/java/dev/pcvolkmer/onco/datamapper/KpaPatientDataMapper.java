package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.datacatalogues.KpaCatalogue;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper class to load and map patient data from database table 'dk_dnpm_kpa'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaPatientDataMapper implements DataMapper<Patient> {

    private final JdbcTemplate jdbcTemplate;

    private KpaPatientDataMapper(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Create instance of the mapper class
     *
     * @param jdbcTemplate The Spring JdbcTemplate to be used
     * @return The initialized mapper
     */
    public static KpaPatientDataMapper create(final JdbcTemplate jdbcTemplate) {
        return new KpaPatientDataMapper(jdbcTemplate);
    }

    /**
     * Loads and maps a patient using the kpa procedures database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded Patient data
     */
    @Override
    public Patient getById(int id) {
        var kpaCatalogue = KpaCatalogue.create(this.jdbcTemplate);
        var kpaData = kpaCatalogue.getById(id);

        var builder = Patient.builder();
        try {
            builder
                    .id(kpaData.getString("patient_id"))
                    .gender(getGenderCoding(kpaData))
                    .birthDate(mapDate(kpaData.getDate("geburtsdatum")))
                    .dateOfDeath(mapDate(kpaData.getDate("sterbedatum")))
                    .healthInsurance(getHealthInsurance(kpaData))
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
            case "m":
                genderCodingBuilder.code(GenderCodingCode.MALE);
                break;
            case "w":
                genderCodingBuilder.code(GenderCodingCode.FEMALE);
                break;
            case "d":
            case "x":
                genderCodingBuilder.code(GenderCodingCode.OTHER);
                break;
            default:
                genderCodingBuilder.code(GenderCodingCode.UNKNOWN);
        }
        return genderCodingBuilder.build();
    }

    private HealthInsurance getHealthInsurance(ResultSet data) throws SQLException {
        var healthInsuranceCodingBuilder = HealthInsuranceCoding.builder();
        String healthInsuranceType = data.getString("artderkrankenkasse");
        switch (healthInsuranceType) {
            case "GKV":
                healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.GKV).build();
                break;
            case "PKV":
                healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.PKV).build();
                break;
            case "BG":
                healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.BG).build();
                break;
            case "SEL":
                healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.SEL).build();
                break;
            case "SOZ":
                healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.SOZ).build();
                break;
            case "GPV":
                healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.GPV).build();
                break;
            case "PPV":
                healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.PPV).build();
                break;
            case "BEI":
                healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.BEI).build();
                break;
            case "SKT":
                healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.SKT).build();
                break;
            default:
                healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.UNK).build();
        }

        return HealthInsurance.builder().type(healthInsuranceCodingBuilder.build()).build();
    }

}
