package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_uf_tumorgrading'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class TumorgradingCatalogue extends AbstractSubformDataCatalogue {

    private TumorgradingCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_uf_tumorgrading";
    }

    public static TumorgradingCatalogue create(JdbcTemplate jdbcTemplate) {
        return new TumorgradingCatalogue(jdbcTemplate);
    }

}
