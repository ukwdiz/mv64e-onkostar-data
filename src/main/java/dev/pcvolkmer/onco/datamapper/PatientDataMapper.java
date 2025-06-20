package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.mv64e.mtb.Address;
import dev.pcvolkmer.mv64e.mtb.GenderCoding;
import dev.pcvolkmer.mv64e.mtb.GenderCodingCode;
import dev.pcvolkmer.mv64e.mtb.Patient;
import dev.pcvolkmer.onco.datamapper.datacatalogues.PatientCatalogue;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static dev.pcvolkmer.onco.datamapper.TypeMapper.asDate;
import static dev.pcvolkmer.onco.datamapper.TypeMapper.asString;

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
        builder
                .id(asString(patientData.get("id")))
                .gender(getGenderCoding(patientData))
                .birthDate(mapDate(asDate(patientData.get("geburtsdatum"))))
                .dateOfDeath(mapDate(asDate(patientData.get("sterbedatum"))))
                .address(Address.builder().municipalityCode(getMunicipalityCode(patientData)).build())
        ;
        return builder.build();
    }

    private GenderCoding getGenderCoding(Map<String, Object> data) {
        var genderCodingBuilder = GenderCoding.builder();
        String geschlecht = asString(data.get("geschlecht"));
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

    private String getMunicipalityCode(Map<String, Object> data) {
        var gkz = asString(data.get("GKZ"));
        if (gkz == null || gkz.trim().length() != 8) {
            throw new DataAccessException("Municipality code not found");
        }
        return gkz.substring(0, 5);
    }

}
