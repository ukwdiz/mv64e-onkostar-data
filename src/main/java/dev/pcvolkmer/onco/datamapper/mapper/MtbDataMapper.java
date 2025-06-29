package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.Mtb;
import dev.pcvolkmer.mv64e.mtb.PriorDiagnosticReport;
import dev.pcvolkmer.mv64e.mtb.Reference;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.*;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to load and map Mtb files from database
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class MtbDataMapper implements DataMapper<Mtb> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataCatalogueFactory catalogueFactory;
    private final PropertyCatalogue propertyCatalogue;

    MtbDataMapper(final JdbcTemplate jdbcTemplate) {
        this.catalogueFactory = DataCatalogueFactory.initialize(jdbcTemplate);
        this.propertyCatalogue = PropertyCatalogue.initialize(jdbcTemplate);
    }

    /**
     * Create instance of the mapper class
     *
     * @param dataSource The datasource to be used
     * @return The initialized mapper
     */
    public static MtbDataMapper create(final DataSource dataSource) {
        return new MtbDataMapper(new JdbcTemplate(dataSource));
    }

    /**
     * Create instance of the mapper class
     *
     * @param jdbcTemplate The Spring JdbcTemplate to be used
     * @return The initialized mapper
     */
    public static MtbDataMapper create(final JdbcTemplate jdbcTemplate) {
        return new MtbDataMapper(jdbcTemplate);
    }

    /**
     * Loads and maps a Mtb file using the root procedures database id
     *
     * @param kpaId The database id of the root procedure data set
     * @return The loaded Mtb file
     */
    @Override
    public Mtb getById(int kpaId) {
        var kpaCatalogue = catalogueFactory.catalogue(KpaCatalogue.class);
        var patientDataMapper = new PatientDataMapper(catalogueFactory.catalogue(PatientCatalogue.class));
        var kpaPatientDataMapper = new KpaPatientDataMapper(kpaCatalogue, propertyCatalogue);
        var diagnosisDataMapper = new KpaDiagnosisDataMapper(
                kpaCatalogue,
                catalogueFactory.catalogue(TumorausbreitungCatalogue.class),
                catalogueFactory.catalogue(TumorgradingCatalogue.class),
                propertyCatalogue
        );
        var mtbEpisodeDataMapper = new MtbEpisodeDataMapper(kpaCatalogue, propertyCatalogue);
        var prozedurMapper = new KpaProzedurDataMapper(
                catalogueFactory.catalogue(ProzedurCatalogue.class),
                propertyCatalogue
        );
        var therapielinieMapper = new KpaTherapielinieDataMapper(
                catalogueFactory.catalogue(TherapielinieCatalogue.class),
                propertyCatalogue
        );
        var ecogMapper = new KpaEcogDataMapper(
                catalogueFactory.catalogue(EcogCatalogue.class)
        );

        var einzelempfehlungCatalogue = catalogueFactory.catalogue(EinzelempfehlungCatalogue.class);
        var therapieplanCatalogue = catalogueFactory.catalogue(TherapieplanCatalogue.class);
        var therapieplanDataMapper = new TherapieplanDataMapper(
                therapieplanCatalogue,
                einzelempfehlungCatalogue,
                propertyCatalogue
        );

        var verwandteDataMapper = new KpaVerwandteDataMapper(catalogueFactory.catalogue(VerwandteCatalogue.class));

        var molekulargenetikCatalogue = catalogueFactory.catalogue(MolekulargenetikCatalogue.class);
        var molekulargenetikToSpecimenDataMapper = new MolekulargenetikToSpecimenDataMapper(
                molekulargenetikCatalogue,
                therapieplanCatalogue,
                catalogueFactory.catalogue(RebiopsieCatalogue.class),
                catalogueFactory.catalogue(ReevaluationCatalogue.class),
                einzelempfehlungCatalogue,
                propertyCatalogue
        );

        var resultBuilder = Mtb.builder();

        try {
            var kpaPatient = kpaPatientDataMapper.getById(kpaId);
            var patient = patientDataMapper.getById(Integer.parseInt(kpaPatient.getId()));
            kpaPatient.setAddress(patient.getAddress());

            var diagnosis = diagnosisDataMapper.getById(kpaId);

            resultBuilder
                    .patient(kpaPatient)
                    .episodesOfCare(List.of(mtbEpisodeDataMapper.getById(kpaId)))
                    // DNPM Klinik/Anamnese
                    .diagnoses(List.of(diagnosis))
                    .guidelineProcedures(prozedurMapper.getByParentId(kpaId))
                    .guidelineTherapies(therapielinieMapper.getByParentId(kpaId))
                    .performanceStatus(ecogMapper.getByParentId(kpaId))
                    .familyMemberHistories(verwandteDataMapper.getByParentId(kpaId))
                    // DNPM Therapieplan
                    .carePlans(
                            therapieplanCatalogue
                                    .getByKpaId(kpaId).stream()
                                    .map(therapieplanDataMapper::getById)
                                    .collect(Collectors.toList())
                    )
                    // Tumorproben
                    .specimens(
                        molekulargenetikToSpecimenDataMapper.getAllByKpaId(
                                kpaId,
                                Reference.builder().id(diagnosis.getId()).type("MTBDiagnosis").build()
                        )
                    )
            ;
        } catch (DataAccessException e) {
            logger.error("Error while getting Mtb.", e);
        }

        return resultBuilder.build();
    }

    /**
     * Loads and maps a Mtb file using the case id
     *
     * @param caseId The case id
     * @return The loaded Mtb file
     */
    public Mtb getByCaseId(String caseId) {
        return this.getById(
                this.catalogueFactory.catalogue(KpaCatalogue.class).getProcedureIdByCaseId(caseId)
        );
    }

    /**
     * Loads and maps a Mtb file using the patient id and tumor id
     *
     * @param patientId The patients id (not database id)
     * @param tumorId The tumor identification
     * @return The loaded Mtb file
     */
    public Mtb getLatestByPatientIdAndTumorId(String patientId, int tumorId) {
        return this.getById(
                this.catalogueFactory.catalogue(KpaCatalogue.class)
                        .getLatestProcedureIdByPatientIdAndTumor(patientId, tumorId)
        );
    }
}
