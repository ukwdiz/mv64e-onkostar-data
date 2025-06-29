package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_uf_rebiopsie'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class RebiopsieCatalogue extends AbstractSubformDataCatalogue {

    private RebiopsieCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_uf_rebiopsie";
    }

    public static RebiopsieCatalogue create(JdbcTemplate jdbcTemplate) {
        return new RebiopsieCatalogue(jdbcTemplate);
    }

}
