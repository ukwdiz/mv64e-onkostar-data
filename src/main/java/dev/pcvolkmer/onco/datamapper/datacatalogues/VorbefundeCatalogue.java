package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_uf_vorbefunde'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class VorbefundeCatalogue extends AbstractSubformDataCatalogue {

    private VorbefundeCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_uf_vorbefunde";
    }

    public static VorbefundeCatalogue create(JdbcTemplate jdbcTemplate) {
        return new VorbefundeCatalogue(jdbcTemplate);
    }

}
