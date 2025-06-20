package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.mv64e.mtb.Address;
import dev.pcvolkmer.mv64e.mtb.GenderCodingCode;
import dev.pcvolkmer.mv64e.mtb.Patient;
import dev.pcvolkmer.onco.datamapper.datacatalogues.PatientCatalogue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class PatientDataMapperTest {

    PatientCatalogue patientCatalogue;

    PatientDataMapper dataMapper;

    @BeforeEach
    void setUp(@Mock PatientCatalogue patientCatalogue) {
        this.patientCatalogue = patientCatalogue;
        this.dataMapper = new PatientDataMapper(patientCatalogue);
    }

    @Test
    void shouldCreateDataMapper(@Mock DataSource dataSource) {
        assertThat(MtbDataMapper.create(dataSource)).isNotNull();
    }

    @Test
    void shouldCreatePatientAlive(@Mock Map<String, Object> resultSet) {
        var testData = Map.of(
                "id", "1",
                "geschlecht", "M",
                "geburtsdatum", new java.sql.Date(Date.from(Instant.parse("2000-01-01T12:00:00Z")).getTime()),
                "sterbedatum", new java.sql.Date(Date.from(Instant.parse("2024-06-19T12:00:00Z")).getTime()),
                "GKZ", "06634022"
        );

        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData.get(columnName);
        }).when(resultSet).get(anyString());

        doAnswer(invocationOnMock -> resultSet)
                .when(patientCatalogue)
                .getById(anyInt());

        var actual = this.dataMapper.getById(1);
        assertThat(actual).isInstanceOf(Patient.class);
        assertThat(actual.getId()).isEqualTo("1");
        assertThat(actual.getGender().getCode()).isEqualTo(GenderCodingCode.MALE);
        assertThat(actual.getBirthDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T12:00:00Z")));
        assertThat(actual.getDateOfDeath()).isEqualTo(Date.from(Instant.parse("2024-06-19T12:00:00Z")));
        assertThat(actual.getAddress()).isEqualTo(Address.builder().municipalityCode("06634").build());
    }

    @Test
    void shouldCreatePatientDead(@Mock Map<String, Object> resultSet) {
        var testData = Map.of(
                "id", "1",
                "geschlecht", "M",
                "geburtsdatum", new java.sql.Date(Date.from(Instant.parse("2000-01-01T12:00:00Z")).getTime()),
                "GKZ", "06634022"
        );

        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData.get(columnName);
        }).when(resultSet).get(anyString());

        doAnswer(invocationOnMock -> resultSet)
                .when(patientCatalogue)
                .getById(anyInt());

        var actual = this.dataMapper.getById(1);
        assertThat(actual).isInstanceOf(Patient.class);
        assertThat(actual.getId()).isEqualTo("1");
        assertThat(actual.getGender().getCode()).isEqualTo(GenderCodingCode.MALE);
        assertThat(actual.getBirthDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T12:00:00Z")));
        assertThat(actual.getDateOfDeath()).isNull();
        assertThat(actual.getAddress()).isEqualTo(Address.builder().municipalityCode("06634").build());
    }

}
