package dev.pcvolkmer.onco.datamapper;

import java.sql.Date;

public class TypeMapper {

    public static String asString(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof String) {
            return (String) raw;
        } else if (raw instanceof Integer) {
            return ((Integer) raw).toString();
        }

        throw new IllegalArgumentException("Cannot convert " + raw.getClass() + " to String");
    }

    public static Date asDate(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Date) {
            return (Date) raw;
        }

        throw new IllegalArgumentException("Cannot convert " + raw.getClass() + " to Date");
    }

}
