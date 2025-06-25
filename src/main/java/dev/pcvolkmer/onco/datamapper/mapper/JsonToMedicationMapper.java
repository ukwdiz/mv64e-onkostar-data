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
        try {
            return new ObjectMapper().readValue(wirkstoffejson, new TypeReference<List<Wirkstoff>>() {
                    }).stream()
                    .map(wirkstoff -> AtcUnregisteredMedicationCoding.builder()
                            .code(wirkstoff.code)
                            .system(
                                    // Wirkstoff ohne Version => UNREGISTERED
                                    "ATC".equals(wirkstoff.system) && null != wirkstoff.version && !wirkstoff.version.isBlank()
                                            ? RequestedMedicationSystem.FHIR_DE_CODE_SYSTEM_BFARM_ATC
                                            : RequestedMedicationSystem.UNDEFINED
                            )
                            .version(wirkstoff.version)
                            .display(wirkstoff.name)
                            .build()
                    )
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
