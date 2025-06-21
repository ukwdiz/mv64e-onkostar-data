package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;

import java.sql.Date;
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
     * Get the procedure id
     *
     * @return The procedure id if any
     */
    public Integer getProcedureId() {
        var procedureId = this.getInteger("procedure.id");
        if (procedureId == null) {
            throw new DataAccessException("No procedure id found");
        }
        return procedureId;
    }

    /**
     * Get the disease id
     *
     * @return The procedure id if any
     */
    public Integer getDiseaseId() {
        var diseaseId = this.getInteger("erkrankung.id");
        if (diseaseId == null) {
            throw new DataAccessException("No disease id found");
        }
        return diseaseId;
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
            return (Date) raw;
        }

        throw new IllegalArgumentException("Cannot convert " + raw.getClass() + " to Date");
    }

}
