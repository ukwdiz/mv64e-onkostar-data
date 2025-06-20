package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_therapielinie'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class TherapielinieCatalogue extends AbstractSubformDataCatalogue {

    private TherapielinieCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_therapielinie";
    }

    public static TherapielinieCatalogue create(JdbcTemplate jdbcTemplate) {
        return new TherapielinieCatalogue(jdbcTemplate);
    }

}
