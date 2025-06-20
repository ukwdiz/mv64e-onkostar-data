package dev.pcvolkmer.onco.datamapper.datacatalogues;

import java.sql.ResultSet;

/**
 * Common interface for all data catalogues
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public interface DataCatalogue {

    /**
     * Get a result set by database id
     * @param id The database id (primary key)
     * @return The result set
     */
    ResultSet getById(int id);

}
