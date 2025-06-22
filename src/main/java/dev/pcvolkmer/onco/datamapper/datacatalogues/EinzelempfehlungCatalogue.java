package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_uf_einzelempfehlung'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class EinzelempfehlungCatalogue extends AbstractSubformDataCatalogue {

    private EinzelempfehlungCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_uf_einzelempfehlung";
    }

    public static EinzelempfehlungCatalogue create(JdbcTemplate jdbcTemplate) {
        return new EinzelempfehlungCatalogue(jdbcTemplate);
    }

}
