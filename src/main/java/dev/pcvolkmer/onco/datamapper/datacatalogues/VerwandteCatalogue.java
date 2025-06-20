package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_uf_verwandte'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class VerwandteCatalogue extends AbstractSubformDataCatalogue {

    private VerwandteCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_uf_verwandte";
    }

    public static VerwandteCatalogue create(JdbcTemplate jdbcTemplate) {
        return new VerwandteCatalogue(jdbcTemplate);
    }

}
