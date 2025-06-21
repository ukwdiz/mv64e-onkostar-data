package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Common implementations for all data catalogues
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public abstract class AbstractDataCatalogue implements DataCatalogue {

    protected final JdbcTemplate jdbcTemplate;

    protected AbstractDataCatalogue(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected abstract String getTableName();

    /**
     * Get procedure result set by procedure id
     *
     * @param id The procedure id
     * @return The procedure id
     */
    @Override
    public ResultSet getById(int id) {
        var result = this.jdbcTemplate.queryForList(
                String.format(
                        "SELECT * FROM %s JOIN prozedur ON (prozedur.id = %s.id) WHERE geloescht = 0 AND prozedur.id = ?",
                        getTableName(),
                        getTableName()
                ),
                id);

        if (result.isEmpty()) {
            throw new DataAccessException("No record found for id: " + id);
        } else if (result.size() > 1) {
            throw new DataAccessException("Multiple records found for id: " + id);
        }

        return ResultSet.from(result.get(0));
    }

    /**
     * Returns related diseases
     * @param procedureId The procedure id
     * @return the diseases
     */
    public List<ResultSet> getDiseases(int procedureId) {
        return this.jdbcTemplate.queryForList(
                        String.format(
                                "SELECT * FROM erkrankung_prozedur JOIN erkrankung ON (erkrankung.id = erkrankung_prozedur.erkrankung_id) WHERE erkrankung_prozedur.prozedur_id = ?",
                                getTableName(),
                                getTableName()
                        ),
                        procedureId)
                .stream()
                .map(ResultSet::from)
                .collect(Collectors.toList());
    }
}
