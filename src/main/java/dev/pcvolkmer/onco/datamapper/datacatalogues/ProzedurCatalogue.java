package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_uf_prozedur'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class ProzedurCatalogue extends AbstractSubformDataCatalogue {

    private ProzedurCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_uf_prozedur";
    }

    public static ProzedurCatalogue create(JdbcTemplate jdbcTemplate) {
        return new ProzedurCatalogue(jdbcTemplate);
    }

}
