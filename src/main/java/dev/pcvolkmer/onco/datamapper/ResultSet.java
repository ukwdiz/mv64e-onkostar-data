package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * Result set type to wrap <code>Map<String, Object></code>
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class ResultSet {

    private final Map<String, Object> rawData;

    private ResultSet(final Map<String, Object> rawData) {
        this.rawData = rawData;
    }

    public static ResultSet from(final Map<String, Object> rawData) {
        return new ResultSet(rawData);
    }

    public Map<String, Object> getRawData() {
        return rawData;
    }

    /**
     * Get the id
     *
     * @return The procedures id
     */
    public Integer getId() {
        var procedureId = this.getInteger("id");
        if (procedureId == null) {
            throw new DataAccessException("No procedure id found");
        }
        return procedureId;
    }

    /**
     * Get column value as String and cast value if possible
     *
     * @param columnName The name of the column
     * @return The column value as String
     */
    public String getString(String columnName) {
        var raw = this.rawData.get(columnName);

        if (raw == null) {
            return null;
        } else if (raw instanceof String) {
            return raw.toString();
        } else if (raw instanceof Integer) {
            return ((Integer) raw).toString();
        }

        throw new IllegalArgumentException("Cannot convert " + raw.getClass() + " to String");
    }

    /**
     * Get column value as Integer and cast value if possible
     *
     * @param columnName The name of the column
     * @return The column value as Integer
     */
    public Integer getInteger(String columnName) {
        var raw = this.rawData.get(columnName);

        if (raw == null) {
            return null;
        } else if (raw instanceof Integer) {
            return ((Integer) raw);
        }

        throw new IllegalArgumentException("Cannot convert " + raw.getClass() + " to Integer");
    }

    /**
     * Get column value as Long and cast value if possible
     *
     * @param columnName The name of the column
     * @return The column value as Integer
     */
    public Long getLong(String columnName) {
        var raw = this.rawData.get(columnName);

        if (raw == null) {
            return null;
        } else if (raw instanceof Integer) {
            return ((Integer) raw).longValue();
        } else if (raw instanceof Long) {
            return ((Long) raw);
        }

        throw new IllegalArgumentException("Cannot convert " + raw.getClass() + " to Integer");
    }

    /**
     * Get column value as Date and cast value if possible
     *
     * @param columnName The name of the column
     * @return The column value as Date
     */
    public Date getDate(String columnName) {
        var raw = this.rawData.get(columnName);

        if (raw == null) {
            return null;
        }
        if (raw instanceof Date) {
            var localDate = LocalDate.parse(raw.toString());
            // JSON Converter uses UTC timezone
            return Date.from(localDate.atStartOfDay(ZoneId.of("UTC")).toInstant());
        }

        throw new IllegalArgumentException("Cannot convert " + raw.getClass() + " to Date");
    }

    /**
     * Check column value is equal to true
     *
     * @param columnName The name of the column
     * @return True if column value is equal to true
     */
    public boolean isTrue(String columnName) {
        var raw = this.rawData.get(columnName);

        if (raw == null) {
            return false;
        }
        if (raw instanceof Boolean) {
            return ((Boolean) raw);
        } else if (raw instanceof Integer) {
            return ((Integer) raw) == 1;
        } else if (raw instanceof Long) {
            return ((Long) raw) == 1;
        } else if (raw instanceof String) {
            return raw.toString().equals("1");
        }

        throw new IllegalArgumentException("Cannot convert " + raw.getClass() + " to Boolean");
    }

}
