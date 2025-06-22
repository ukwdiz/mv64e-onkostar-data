package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Load raw result sets from database table 'dk_dnpm_therapieplan'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class TherapieplanCatalogue extends AbstractDataCatalogue {

    private TherapieplanCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getTableName() {
        return "dk_dnpm_therapieplan";
    }

    public static TherapieplanCatalogue create(JdbcTemplate jdbcTemplate) {
        return new TherapieplanCatalogue(jdbcTemplate);
    }

    /**
     * Get procedure IDs by related Klinik/Anamnese procedure id
     *
     * @param kpaId The procedure id
     * @return The procedure ids
     */
    public List<Integer> getByKpaId(int kpaId) {
        return this.jdbcTemplate.queryForList(
                String.format(
                        "SELECT prozedur.id AS procedure_id FROM %s JOIN prozedur ON (prozedur.id = %s.id) WHERE geloescht = 0 AND ref_dnpm_klinikanamnese = ?",
                        getTableName(),
                        getTableName()
                ),
                kpaId)
                .stream()
                .map(ResultSet::from)
                .map(rs -> rs.getInteger("procedure_id"))
                .collect(Collectors.toList());
    }

}
