package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.MtbEpisodeOfCare;
import dev.pcvolkmer.mv64e.mtb.PeriodDate;
import dev.pcvolkmer.mv64e.mtb.Reference;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.KpaCatalogue;

import java.util.List;

import static dev.pcvolkmer.onco.datamapper.mapper.MapperUtils.getPatientReference;

/**
 * Mapper class to load and map patient data from database table 'dk_dnpm_kpa'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class MtbEpisodeDataMapper implements DataMapper<MtbEpisodeOfCare> {

    private final KpaCatalogue kpaCatalogue;
    private final PropertyCatalogue propertyCatalogue;


    public MtbEpisodeDataMapper(
            final KpaCatalogue kpaCatalogue,
            final PropertyCatalogue propertyCatalogue
    ) {
        this.kpaCatalogue = kpaCatalogue;
        this.propertyCatalogue = propertyCatalogue;
    }

    /**
     * Loads and maps a ca plan using the database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded Patient data
     */
    @Override
    public MtbEpisodeOfCare getById(int id) {
        var kpaData = kpaCatalogue.getById(id);

        var builder = MtbEpisodeOfCare.builder();
        builder
                .id(kpaData.getString("id"))
                .patient(getPatientReference(kpaData.getString("patient_id")))
                .diagnoses(
                        List.of(
                                Reference.builder()
                                        .id(kpaData.getString("id"))
                                        .type("Diagnose")
                                        .build()
                        )
                )
                .period(PeriodDate.builder().start(kpaData.getDate("anmeldedatummtb")).build())
                .build()
        ;
        return builder.build();
    }

}
