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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.ProzedurCatalogue;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KpaProzedurDataMapperTest {

  ProzedurCatalogue catalogue;
  PropertyCatalogue propertyCatalogue;

  KpaProzedurDataMapper dataMapper;

  @BeforeEach
  void setUp(@Mock ProzedurCatalogue catalogue, @Mock PropertyCatalogue propertyCatalogue) {
    this.catalogue = catalogue;
    this.propertyCatalogue = propertyCatalogue;
    this.dataMapper = new KpaProzedurDataMapper(catalogue, propertyCatalogue);
  }

  @Test
  void shouldMapResultSet(@Mock ResultSet resultSet) {
    var testData =
        Map.of(
            "id", "1",
            "beginn", new java.sql.Date(Date.from(Instant.parse("2000-01-01T12:00:00Z")).getTime()),
            "ende", new java.sql.Date(Date.from(Instant.parse("2024-06-19T12:00:00Z")).getTime()),
            "erfassungsdatum",
                new java.sql.Date(Date.from(Instant.parse("2024-06-19T12:00:00Z")).getTime()),
            "intention", "S",
            "status", "stopped",
            "statusgrund", "patient-death",
            "therapielinie", 1L,
            "typ", "surgery",
            "patienten_id", "42");

    doAnswer(
            invocationOnMock ->
                Reference.builder()
                    .id(testData.get("patienten_id").toString())
                    .type("Patient")
                    .build())
        .when(resultSet)
        .getPatientReference();

    doAnswer(
            invocationOnMock -> {
              var columnName = invocationOnMock.getArgument(0, String.class);
              return testData.get(columnName);
            })
        .when(resultSet)
        .getString(anyString());

    doAnswer(
            invocationOnMock -> {
              var columnName = invocationOnMock.getArgument(0, String.class);
              return testData.get(columnName);
            })
        .when(resultSet)
        .getLong(anyString());

    doAnswer(
            invocationOnMock -> {
              var columnName = invocationOnMock.getArgument(0, String.class);
              return testData.get(columnName);
            })
        .when(resultSet)
        .getDate(anyString());

    when(resultSet.getInteger(anyString())).thenReturn(1);
    when(resultSet.getId()).thenReturn(1);

    doAnswer(invocationOnMock -> List.of(resultSet)).when(catalogue).getAllByParentId(anyInt());

    doAnswer(invocationOnMock -> List.of(resultSet)).when(catalogue).getDiseases(anyInt());

    doAnswer(
            invocationOnMock -> {
              var testPropertyData =
                  Map.of(
                      "S", new PropertyCatalogue.Entry("S", "Sonstiges", "Sonstiges"),
                      "stopped",
                          new PropertyCatalogue.Entry("stopped", "Abgebrochen", "Abgebrochen"),
                      "patient-death", new PropertyCatalogue.Entry("patient-death", "Tod", "Tod"),
                      "surgery", new PropertyCatalogue.Entry("surgery", "OP", "OP"));

              var code = invocationOnMock.getArgument(0, String.class);
              return testPropertyData.get(code);
            })
        .when(propertyCatalogue)
        .getByCodeAndVersion(anyString(), anyInt());

    var actualList = this.dataMapper.getByParentId(1);
    /*assertThat(actualList).hasSize(1);

    var actual = actualList.get(0);
    assertThat(actual).isInstanceOf(OncoProcedure.class);
    assertThat(actual.getId()).isEqualTo("1");
    assertThat(actual.getPatient())
            .isEqualTo(Reference.builder().id("42").type("Patient").build());
    assertThat(actual.getPeriod()).isEqualTo(
            PeriodDate.builder()
                    .start(Date.from(Instant.parse("2000-01-01T12:00:00Z")))
                    .end(Date.from(Instant.parse("2024-06-19T12:00:00Z")))
                    .build()
    );
    assertThat(actual.getRecordedOn()).isEqualTo(Date.from(Instant.parse("2024-06-19T12:00:00Z")));
    assertThat(actual.getIntent())
            .isEqualTo(
                    MtbTherapyIntentCoding.builder()
                            .code(MtbTherapyIntentCodingCode.S)
                            .display("Sonstiges")
                            .system("dnpm-dip/therapy/intent")
                            .build()
            );
    assertThat(actual.getStatus())
            .isEqualTo(
                    TherapyStatusCoding.builder()
                            .code(TherapyStatusCodingCode.STOPPED)
                            .display("Abgebrochen")
                            .system("dnpm-dip/therapy/status")
                            .build()
            );
    assertThat(actual.getStatusReason())
            .isEqualTo(
                    MtbTherapyStatusReasonCoding.builder()
                            .code(MtbTherapyStatusReasonCodingCode.PATIENT_DEATH)
                            .display("Tod")
                            .system("dnpm-dip/therapy/status-reason")
                            .build()
            );
    assertThat(actual.getTherapyLine()).isEqualTo(1);
    assertThat(actual.getCode())
            .isEqualTo(
                    OncoProcedureCoding.builder()
                            .code(OncoProcedureCodingCode.SURGERY)
                            .display("OP")
                            .system("dnpm-dip/therapy/type")
                            .build()
            );*/
  }
}
