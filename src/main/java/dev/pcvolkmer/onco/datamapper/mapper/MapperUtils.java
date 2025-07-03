/*
 * This file is part of mv64e-onkostar-data
 *
 * Copyright (C) 2025  Paul-Christian Volkmer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
