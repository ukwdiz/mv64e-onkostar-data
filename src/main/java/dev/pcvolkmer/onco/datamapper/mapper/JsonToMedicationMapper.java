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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pcvolkmer.mv64e.mtb.AtcUnregisteredMedicationCoding;
import dev.pcvolkmer.mv64e.mtb.RequestedMedicationSystem;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps JSON strings used in form into DNPM medication
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class JsonToMedicationMapper {

  private JsonToMedicationMapper() {
    // intentionally left empty
  }

  public static List<AtcUnregisteredMedicationCoding> map(String wirkstoffejson) {
    if (wirkstoffejson == null) {
      return List.of();
    }
    try {
      return new ObjectMapper()
          .readValue(wirkstoffejson, new TypeReference<List<Wirkstoff>>() {}).stream()
              .map(
                  wirkstoff ->
                      AtcUnregisteredMedicationCoding.builder()
                          .code(wirkstoff.code)
                          .system(
                              // Wirkstoff ohne Version => UNREGISTERED
                              "ATC".equals(wirkstoff.system)
                                      && null != wirkstoff.version
                                      && !wirkstoff.version.isBlank()
                                  ? RequestedMedicationSystem.FHIR_DE_CODE_SYSTEM_BFARM_ATC
                                  : RequestedMedicationSystem.UNDEFINED)
                          .version(wirkstoff.version)
                          .display(wirkstoff.name)
                          .build())
              .collect(Collectors.toList());
    } catch (Exception e) {
      throw new DataAccessException(String.format("Cannot map medication for %s", wirkstoffejson));
    }
  }

  private static class Wirkstoff {
    private String code;

    @JsonAlias("substance")
    private String name;

    private String system;
    private String version;

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getSystem() {
      return system;
    }

    public void setSystem(String system) {
      this.system = system;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }
  }
}
