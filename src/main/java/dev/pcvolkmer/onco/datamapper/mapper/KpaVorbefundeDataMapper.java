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

import dev.pcvolkmer.mv64e.mtb.EcogCoding;
import dev.pcvolkmer.mv64e.mtb.EcogCodingCode;
import dev.pcvolkmer.mv64e.mtb.PerformanceStatus;
import dev.pcvolkmer.mv64e.mtb.PriorDiagnosticReport;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.EcogCatalogue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dev.pcvolkmer.onco.datamapper.mapper.MapperUtils.getPatientReference;

/**
 * Mapper class to load and map prozedur data from database table 'dk_dnpm_vorbefunde'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaVorbefundeDataMapper extends AbstractSubformDataMapper<PriorDiagnosticReport> {

    public KpaVorbefundeDataMapper(final EcogCatalogue catalogue) {
        super(catalogue);
    }

    /**
     * Loads and maps Prozedur related by database id
     *
     * @param id The database id of the procedure data set
     * @return The loaded data set
     */
    @Override
    public PriorDiagnosticReport getById(final int id) {
        var data = catalogue.getById(id);
        return this.map(data);
    }

    @Override
    public List<PriorDiagnosticReport> getByParentId(final int parentId) {
        return catalogue.getAllByParentId(parentId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    protected PriorDiagnosticReport map(final ResultSet resultSet) {
        var builder = PriorDiagnosticReport.builder();
        builder
                .id(resultSet.getId().toString())
                .patient(getPatientReference(resultSet.getString("patient_id")))
                .issuedOn(resultSet.getDate("datum"))
                .results(List.of(resultSet.getString("ecog")))
        ;

        return builder.build();
    }

}
