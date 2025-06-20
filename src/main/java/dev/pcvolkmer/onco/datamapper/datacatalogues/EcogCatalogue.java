package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_uf_ecog'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class EcogCatalogue extends AbstractSubformDataCatalogue {

    private EcogCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_uf_ecog";
    }

    public static EcogCatalogue create(JdbcTemplate jdbcTemplate) {
        return new EcogCatalogue(jdbcTemplate);
    }

}
