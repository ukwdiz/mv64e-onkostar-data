package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.ResultSet;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Common implementations for all data catalogues used in subforms
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public abstract class AbstractSubformDataCatalogue extends AbstractDataCatalogue implements DataCatalogue {

    protected AbstractSubformDataCatalogue(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    protected abstract String getTableName();

    /**
     * Get procedure result sets by parent procedure id
     *
     * @param id The parents procedure id
     * @return The sub procedures
     */
    public List<ResultSet> getAllByParentId(int id) {
        return this.jdbcTemplate.queryForList(
                        String.format(
                                "SELECT * FROM %s JOIN prozedur ON (prozedur.id = %s.id) WHERE geloescht = 0 AND hauptprozedur_id = ?",
                                getTableName(),
                                getTableName()
                        ),
                        id)
                .stream()
                .map(ResultSet::from)
                .collect(Collectors.toList());
    }

}
