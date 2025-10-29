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

import dev.pcvolkmer.mv64e.mtb.MolecularDiagnosticReportCoding;
import dev.pcvolkmer.mv64e.mtb.MolecularDiagnosticReportCodingCode;
import dev.pcvolkmer.mv64e.mtb.PriorDiagnosticReport;
import dev.pcvolkmer.mv64e.mtb.Reference;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.MolekulargenetikCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.VorbefundeCatalogue;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper class to load and map prozedur data from database table 'dk_dnpm_vorbefunde'
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class KpaVorbefundeDataMapper extends AbstractSubformDataMapper<PriorDiagnosticReport> {

  private final MolekulargenetikCatalogue molekulargenetikCatalogue;
  private final PropertyCatalogue propertyCatalogue;

  public KpaVorbefundeDataMapper(
      final VorbefundeCatalogue catalogue,
      final MolekulargenetikCatalogue molekulargenetikCatalogue,
      final PropertyCatalogue propertyCatalogue) {
    super(catalogue);
    this.molekulargenetikCatalogue = molekulargenetikCatalogue;
    this.propertyCatalogue = propertyCatalogue;
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
    return catalogue.getAllByParentId(parentId).stream()
        .map(this::map)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  @Override
  protected PriorDiagnosticReport map(final ResultSet resultSet) {
    var builder = PriorDiagnosticReport.builder();
    var einsendenummer = resultSet.getString("befundnummer");

    var osMolGen = molekulargenetikCatalogue.getByEinsendenummer(einsendenummer);

    if (null != osMolGen) {
      builder
          .id(resultSet.getId().toString())
          .patient(resultSet.getPatientReference())
          .issuedOn(resultSet.getDate("erstellungsdatum"))
          .specimen(Reference.builder().id(osMolGen.getId().toString()).type("Specimen").build())
          .type(
              getMolecularDiagnosticReportCoding(
                  resultSet.getString("artderdiagnostik"),
                  resultSet.getInteger("artderdiagnostik_propcat_version")))
          .results(List.of(resultSet.getString("ergebnisse")));

      return builder.build();
    }

    return null;
  }

  private MolecularDiagnosticReportCoding getMolecularDiagnosticReportCoding(
      String value, int version) {
    if (value == null
        || !Arrays.stream(MolecularDiagnosticReportCodingCode.values())
            .map(MolecularDiagnosticReportCodingCode::toValue)
            .collect(Collectors.toSet())
            .contains(value)) {
      return null;
    }

    var resultBuilder =
        MolecularDiagnosticReportCoding.builder()
            .display(propertyCatalogue.getByCodeAndVersion(value, version).getShortdesc());
    try {
      resultBuilder.code(MolecularDiagnosticReportCodingCode.forValue(value));
    } catch (IOException e) {
      return null;
    }

    return resultBuilder.build();
  }
}
