package dev.pcvolkmer.onco.datamapper.mapper;

/**
 * General interface for all data mappers
 *
 * @param <T> The destination type
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public interface DataMapper<T> {

    /**
     * Loads a data set from database and maps it into destination data type
     *
     * @param id The database id of the root procedure data set
     * @return The data set to be loaded
     */
    T getById(int id);

}
