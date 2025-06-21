package dev.pcvolkmer.onco.datamapper.mapper;

import java.util.Date;

/**
 * General interface for all data mappers
 *
 * @since 0.1
 * @author Paul-Christian Volkmer
 * @param <T> The destination type
 */
public interface DataMapper<T> {

    /**
     * Loads a data set from database and maps it into destination data type
     * @param id The database id of the root procedure data set
     * @return The data set to be loaded
     */
    T getById(int id);

    /**
     * Maps java.sql.Date to java.util.Date
     * @param date
     * @return
     */
    default Date mapDate(java.sql.Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime());
    }

}
