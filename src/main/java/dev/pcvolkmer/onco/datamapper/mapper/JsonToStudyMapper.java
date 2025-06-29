package dev.pcvolkmer.onco.datamapper.mapper;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pcvolkmer.mv64e.mtb.StudyReference;
import dev.pcvolkmer.mv64e.mtb.StudySystem;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps JSON strings used in form into DNPM study
 *
 * @author Paul-Christian Volkmer
 * @since 0.1
 */
public class JsonToStudyMapper {

    private JsonToStudyMapper() {
        // intentionally left empty
    }

    public static List<StudyReference> map(String studyJson) {
        if (studyJson == null) {
            return List.of();
        }
        try {
            return new ObjectMapper().readValue(studyJson, new TypeReference<List<Studie>>() {
                    }).stream()
                    .map(studie -> StudyReference.builder()
                            .id(studie.id)
                            .system(getStudySystem(studie.system))
                            .type("Study")
                            .build()
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataAccessException(String.format("Cannot map medication for %s", studyJson));
        }
    }

    private static StudySystem getStudySystem(String code) {
        if (code == null || !Arrays.stream(StudySystem.values()).map(StudySystem::toValue).collect(Collectors.toSet()).contains(code)) {
            return null;
        }

        try {
            return StudySystem.valueOf(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Studie {
        @JsonAlias("nct")
        private String id;
        private String system;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }
    }

}
