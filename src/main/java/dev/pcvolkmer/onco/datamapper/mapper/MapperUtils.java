package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.Reference;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;

/**
 * Utility methods to be used in mappers
 */
public class MapperUtils {

    private MapperUtils() {
    }

    /**
     * Get Patient Reference based on default column name 'patient_id'
     *
     * @return The patient reference
     */
    public static Reference getPatientReference(String patientId) {
        if (patientId == null) {
            throw new DataAccessException("No patient id found");
        }
        return Reference.builder()
                .id(patientId)
                // Use "Patient" since Onkostar only provides patient data
                .type("Patient")
                .build();
    }

}
