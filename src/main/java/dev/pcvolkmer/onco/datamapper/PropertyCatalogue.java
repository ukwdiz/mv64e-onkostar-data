package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Data access to property catalogues in Onkostar database
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class PropertyCatalogue {

    private final JdbcTemplate jdbcTemplate;

    private PropertyCatalogue(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static PropertyCatalogue obj;

    public static synchronized PropertyCatalogue initialize(final JdbcTemplate jdbcTemplate) {
        if (null == obj) {
            obj = new PropertyCatalogue(jdbcTemplate);
        }
        return obj;
    }

    public static synchronized PropertyCatalogue instance() {
        if (null == obj) {
            throw new IllegalStateException("PropertyCatalogue not initialized");
        }
        return obj;
    }

    /**
     * Get procedure result sets by parent procedure id
     *
     * @param code    The entries code
     * @param version The entries version
     * @return The sub procedures
     */
    public Entry getByCodeAndVersion(String code, int version) {
        try {
            return this.jdbcTemplate.queryForObject(
                    "SELECT code, shortdesc, description FROM property_catalogue_version_entry WHERE code = ? AND property_version_id = ?",
                    (rs, rowNum) -> new Entry(rs.getString("code"), rs.getString("shortdesc"), rs.getString("description")),
                    code,
                    version);
        } catch (RuntimeException e) {
            throw new DataAccessException(String.format("Cannot request property catalogue entry for '%s' version '%d'", code, version));
        }
    }

    /**
     * A property catalogue entry
     */
    public static class Entry {
        private final String code;
        private final String shortdesc;
        private final String description;

        public Entry(String code, String shortdesc, String description) {
            this.code = code;
            this.shortdesc = shortdesc;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public String getShortdesc() {
            return shortdesc;
        }
    }

}
