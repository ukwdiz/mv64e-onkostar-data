package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.AbstractSubformDataCatalogue;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract common implementation for all subform data mappers
 *
 * @since 0.1
 * @author Paul-Christian Volkmer
 * @param <T> The destination type
 */
public abstract class AbstractSubformDataMapper<T> implements SubformDataMapper<T> {

    protected AbstractSubformDataCatalogue catalogue;

    protected AbstractSubformDataMapper(AbstractSubformDataCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    /**
     * Loads a data set from database and maps it into destination data type
     * @param parentId The database id of the parent procedure data set
     * @return The data set to be loaded
     */
    @Override
    public List<T> getByParentId(final int parentId) {
        return catalogue.getAllByParentId(parentId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    /**
     * Maps a single result set into destination object
     * @param resultSet The result set to start from
     * @return The destination object
     */
    protected abstract T map(ResultSet resultSet);

}
