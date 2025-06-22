package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.MtbCarePlan;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TherapieplanCatalogue;

import static dev.pcvolkmer.onco.datamapper.mapper.MapperUtils.getPatientReference;

/**
 * Mapper class to load and map patient data from database table 'dk_dnpm_therapieplan'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class TherapieplanDataMapper implements DataMapper<MtbCarePlan> {

    private final TherapieplanCatalogue therapieplanCatalogue;
    private final PropertyCatalogue propertyCatalogue;

    public TherapieplanDataMapper(
            final TherapieplanCatalogue therapieplanCatalogue,
            final PropertyCatalogue propertyCatalogue
    ) {
        this.therapieplanCatalogue = therapieplanCatalogue;
        this.propertyCatalogue = propertyCatalogue;
    }

    /**
     * Loads and maps a ca plan using the database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded Patient data
     */
    @Override
    public MtbCarePlan getById(int id) {
        var therapieplanData = therapieplanCatalogue.getById(id);

        var builder = MtbCarePlan.builder();
        builder
                .id(therapieplanData.getString("id"))
                .patient(getPatientReference(therapieplanData.getString("patient_id")))
                .issuedOn(therapieplanData.getDate("datum"))
        ;
        return builder.build();
    }

}
