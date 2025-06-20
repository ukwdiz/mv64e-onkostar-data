package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_uf_tumorausbreitung'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class TumorausbreitungCatalogue extends AbstractSubformDataCatalogue {

    private TumorausbreitungCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_uf_tumorausbreitung";
    }

    public static TumorausbreitungCatalogue create(JdbcTemplate jdbcTemplate) {
        return new TumorausbreitungCatalogue(jdbcTemplate);
    }

}
