package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple catalogue factory to get a catalogue instance
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class CatalogueFactory {

    private final JdbcTemplate jdbcTemplate;
    private final Map<Class<? extends DataCatalogue>, DataCatalogue> catalogues = new HashMap<>();

    private CatalogueFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static CatalogueFactory obj;

    public static synchronized CatalogueFactory instance(final JdbcTemplate jdbcTemplate) {
        if (null == obj) {
            obj = new CatalogueFactory(jdbcTemplate);
        }
        return obj;
    }

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
            }
            throw new RuntimeException("Unknown DataCatalogue class: " + clazz);
        });
    }

}
