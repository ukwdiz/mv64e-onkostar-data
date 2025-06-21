package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.KpaCatalogue;

/**
 * Mapper class to load and map patient data from database table 'dk_dnpm_kpa'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaPatientDataMapper implements DataMapper<Patient> {

    private final KpaCatalogue kpaCatalogue;

    public KpaPatientDataMapper(final KpaCatalogue kpaCatalogue) {
        this.kpaCatalogue = kpaCatalogue;
    }

    /**
     * Loads and maps a patient using the kpa procedures database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded Patient data
     */
    @Override
    public Patient getById(int id) {
        var kpaData = kpaCatalogue.getById(id);

        var builder = Patient.builder();
        builder
                .id(kpaData.getString("patient_id"))
                .gender(getGenderCoding(kpaData))
                .birthDate(kpaData.getDate("geburtsdatum"))
                .dateOfDeath(kpaData.getDate("todesdatum"))
                .healthInsurance(getHealthInsurance(kpaData))
        ;
        return builder.build();
    }

    private GenderCoding getGenderCoding(ResultSet data) {
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

    private HealthInsurance getHealthInsurance(ResultSet data) {
        var healthInsuranceCodingBuilder = HealthInsuranceCoding.builder();
        String healthInsuranceType = data.getString("artderkrankenkasse");
        if (healthInsuranceType == null) {
            healthInsuranceCodingBuilder.code(HealthInsuranceCodingCode.UNK).build();
            return HealthInsurance.builder().type(healthInsuranceCodingBuilder.build()).build();
        }

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
