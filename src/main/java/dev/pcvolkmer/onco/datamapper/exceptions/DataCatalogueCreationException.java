package dev.pcvolkmer.onco.datamapper.exceptions;

import dev.pcvolkmer.onco.datamapper.datacatalogues.DataCatalogue;

/**
 * Exception to be thrown if no catalogue can be created
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class DataCatalogueCreationException extends RuntimeException {
    public DataCatalogueCreationException(Class<? extends DataCatalogue> clazz) {
        super(String.format("Error creating data catalogue for type '%s'", clazz.getCanonicalName()));
    }
}
