package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.MtbSystemicTherapy;
import dev.pcvolkmer.mv64e.mtb.PeriodDate;
import dev.pcvolkmer.mv64e.mtb.Reference;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TherapielinieCatalogue;

/**
 * Mapper class to load and map prozedur data from database table 'dk_dnpm_therapielinie'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaTherapielinieDataMapper extends AbstractKpaTherapieverlaufDataMapper<MtbSystemicTherapy> {

    public KpaTherapielinieDataMapper(final TherapielinieCatalogue catalogue, final PropertyCatalogue propertyCatalogue) {
        super(catalogue, propertyCatalogue);
    }

    /**
     * Loads and maps Prozedur related by database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded MtbDiagnosis file
     */
    @Override
    public MtbSystemicTherapy getById(final int id) {
        var data = catalogue.getById(id);
        return this.map(data);
    }

    @Override
    protected MtbSystemicTherapy map(final ResultSet resultSet) {
        var diseases = catalogue.getDiseases(resultSet.getProcedureId());

        if (diseases.size() != 1) {
            throw new IllegalStateException(String.format("No unique disease for procedure %s", resultSet.getProcedureId()));
        }

        var builder = MtbSystemicTherapy.builder();
        builder
                .id(resultSet.getString("id"))
                .patient(Reference.builder().id(resultSet.getString("patient_id")).build())
                .basedOn(Reference.builder().id(diseases.get(0).getDiseaseId().toString()).build())
                .recordedOn(resultSet.getDate("erfassungsdatum"))
                .therapyLine(resultSet.getLong("therapielinie"))
                .intent(
                        getMtbTherapyIntentCoding(
                                resultSet.getString("intention"),
                                resultSet.getInteger("intention_propcat_version")
                        )
                )
                .status(
                        getTherapyStatusCoding(
                                resultSet.getString("status"),
                                resultSet.getInteger("status_propcat_version")
                        )
                )
                .statusReason(
                        getMtbTherapyStatusReasonCoding(
                                resultSet.getString("statusgrund"),
                                resultSet.getInteger("statusgrund_propcat_version")
                        )
                )
                .period(
                        PeriodDate.builder()
                                .start(resultSet.getDate("beginn"))
                                .end(resultSet.getDate("ende"))
                                .build()
                )
        /* TODO JSON deserialisation */
        //.medication()

        /* TODO Yet missing form fields */
        //.category(getMtbSystemicTherapyCategoryCoding())
        //.dosage(getMtbSystemicTherapyDosageDensityCoding())
        //.recommendationFulfillmentStatus(getMtbSystemicTherapyRecommendationFulfillmentStatusCoding()
        ;
        return builder.build();
    }


}
