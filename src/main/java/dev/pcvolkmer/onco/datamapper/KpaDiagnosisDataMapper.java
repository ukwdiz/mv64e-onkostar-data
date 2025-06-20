package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.mv64e.mtb.Coding;
import dev.pcvolkmer.mv64e.mtb.MtbDiagnosis;
import dev.pcvolkmer.onco.datamapper.datacatalogues.KpaCatalogue;

import static dev.pcvolkmer.onco.datamapper.TypeMapper.asString;

/**
 * Mapper class to load and map diagnosis data from database table 'dk_dnpm_kpa'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaDiagnosisDataMapper implements DataMapper<MtbDiagnosis> {

    private final KpaCatalogue kpaCatalogue;

    public KpaDiagnosisDataMapper(final KpaCatalogue kpaCatalogue) {
        this.kpaCatalogue = kpaCatalogue;
    }

    /**
     * Loads and maps a diagnosis using the kpa procedures database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded MtbDiagnosis file
     */
    @Override
    public MtbDiagnosis getById(int id) {
        var data = kpaCatalogue.getById(id);

        var builder =  MtbDiagnosis.builder();
        builder
                .id(asString(data.get("id")))
                .code(
                        Coding.builder()
                                .code(asString(data.get("icd10")))
                                .build()
                );
        return builder.build();
    }

}
