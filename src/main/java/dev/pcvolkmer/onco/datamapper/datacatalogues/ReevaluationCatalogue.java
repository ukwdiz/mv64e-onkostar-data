package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Load raw result sets from database table 'dk_dnpm_uf_reevaluation'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class ReevaluationCatalogue extends AbstractSubformDataCatalogue {

    private ReevaluationCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_uf_reevaluation";
    }

    public static ReevaluationCatalogue create(JdbcTemplate jdbcTemplate) {
        return new ReevaluationCatalogue(jdbcTemplate);
    }

}
