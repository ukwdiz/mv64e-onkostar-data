package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.exceptions.DataCatalogueCreationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple catalogue factory to get a catalogue instance
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class DataCatalogueFactory {

    private final JdbcTemplate jdbcTemplate;
    private final Map<Class<? extends DataCatalogue>, DataCatalogue> catalogues = new HashMap<>();

    private DataCatalogueFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static DataCatalogueFactory obj;

    public static synchronized DataCatalogueFactory initialize(final JdbcTemplate jdbcTemplate) {
        if (null == obj) {
            obj = new DataCatalogueFactory(jdbcTemplate);
        }
        return obj;
    }

    public static synchronized DataCatalogueFactory instance() {
        if (null == obj) {
            throw new IllegalStateException("CatalogueFactory not initialized");
        }
        return obj;
    }

    /**
     * Get Catalogue of required type
     *
     * @param clazz The catalogues class
     * @param <T>   The catalogue type
     * @return The catalogue if it exists
     */
    @SuppressWarnings("unchecked")
    public synchronized <T extends DataCatalogue> T catalogue(Class<T> clazz) {
        return (T) catalogues.computeIfAbsent(clazz, c -> {
            if (c == EcogCatalogue.class) {
                return EcogCatalogue.create(jdbcTemplate);
            } else if (c == HistologieCatalogue.class) {
                return HistologieCatalogue.create(jdbcTemplate);
            } else if (c == KpaCatalogue.class) {
                return KpaCatalogue.create(jdbcTemplate);
            } else if (c == PatientCatalogue.class) {
                return PatientCatalogue.create(jdbcTemplate);
            } else if (c == ProzedurCatalogue.class) {
                return ProzedurCatalogue.create(jdbcTemplate);
            } else if (c == TherapielinieCatalogue.class) {
                return TherapielinieCatalogue.create(jdbcTemplate);
            } else if (c == TumorausbreitungCatalogue.class) {
                return TumorausbreitungCatalogue.create(jdbcTemplate);
            } else if (c == TumorgradingCatalogue.class) {
                return TumorgradingCatalogue.create(jdbcTemplate);
            } else if (c == VerwandteCatalogue.class) {
                return VerwandteCatalogue.create(jdbcTemplate);
            } else if (c == VorbefundeCatalogue.class) {
                return VorbefundeCatalogue.create(jdbcTemplate);
            } else if (c == TherapieplanCatalogue.class) {
                return TherapieplanCatalogue.create(jdbcTemplate);
            }
            throw new DataCatalogueCreationException(clazz);
        });
    }

    /**
     * Checks if a catalogue of this type is available
     *
     * @param clazz The catalogues class
     * @return true if it is available
     */
    public synchronized boolean hasCatalogue(Class<? extends DataCatalogue> clazz) {
        return catalogues.containsKey(clazz);
    }

}
