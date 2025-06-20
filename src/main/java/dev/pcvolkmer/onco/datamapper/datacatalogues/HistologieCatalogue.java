package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_uf_histologie'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class HistologieCatalogue extends AbstractSubformDataCatalogue {

    private HistologieCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_uf_histologie";
    }

    public static HistologieCatalogue create(JdbcTemplate jdbcTemplate) {
        return new HistologieCatalogue(jdbcTemplate);
    }

}
