package dev.pcvolkmer.onco.datamapper.mapper;

import java.util.List;

/**
 * General interface for subform data mappers
 *
 * @since 0.1
 * @author Paul-Christian Volkmer
 * @param <T> The destination type
 */
public interface SubformDataMapper<T> extends DataMapper<T> {

    /**
     * Loads a data set from database and maps it into destination data type
     * @param parentId The database id of the parent procedure data set
     * @return The data set to be loaded
     */
    List<T> getByParentId(int parentId);

}
