package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_molekulargenetik'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class MolekulargenetikCatalogue extends AbstractSubformDataCatalogue {

    private MolekulargenetikCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_molekulargenetik";
    }

    public static MolekulargenetikCatalogue create(JdbcTemplate jdbcTemplate) {
        return new MolekulargenetikCatalogue(jdbcTemplate);
    }

}
