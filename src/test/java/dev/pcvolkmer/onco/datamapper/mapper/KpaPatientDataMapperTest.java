package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.datacatalogues.KpaCatalogue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class KpaPatientDataMapperTest {

    KpaCatalogue kpaCatalogue;

    KpaPatientDataMapper dataMapper;

    @BeforeEach
    void setUp(@Mock KpaCatalogue kpaCatalogue) {
        this.kpaCatalogue = kpaCatalogue;
        this.dataMapper = new KpaPatientDataMapper(kpaCatalogue);
    }

    @Test
    void shouldCreateDataMapper(@Mock DataSource dataSource) {
        assertThat(MtbDataMapper.create(dataSource)).isNotNull();
    }

    @Test
    void shouldCreatePatientAlive(@Mock Map<String, Object> resultSet) {
        var testData = Map.of(
                "patient_id", "1",
                "geschlecht", "m",
                "geburtsdatum", new java.sql.Date(Date.from(Instant.parse("2000-01-01T12:00:00Z")).getTime()),
                "todesdatum", new java.sql.Date(Date.from(Instant.parse("2024-06-19T12:00:00Z")).getTime()),
                "artderkrankenkasse", "GKV"
        );

        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData.get(columnName);
        }).when(resultSet).get(anyString());

        doAnswer(invocationOnMock -> resultSet)
                .when(kpaCatalogue)
                .getById(anyInt());

        var actual = this.dataMapper.getById(1);
        assertThat(actual).isInstanceOf(Patient.class);
        assertThat(actual.getId()).isEqualTo("1");
        assertThat(actual.getGender().getCode()).isEqualTo(GenderCodingCode.MALE);
        assertThat(actual.getBirthDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T12:00:00Z")));
        assertThat(actual.getDateOfDeath()).isEqualTo(Date.from(Instant.parse("2024-06-19T12:00:00Z")));
        assertThat(actual.getHealthInsurance()).isEqualTo(
                HealthInsurance.builder().type(HealthInsuranceCoding.builder().code(HealthInsuranceCodingCode.GKV).build()).build()
        );
    }

    @Test
    void shouldCreatePatientDead(@Mock Map<String, Object> resultSet) {
        var testData = Map.of(
                "patient_id", "1",
                "geschlecht", "w",
                "geburtsdatum", new java.sql.Date(Date.from(Instant.parse("2000-01-01T12:00:00Z")).getTime()),
                "artderkrankenkasse", "PKV"
        );

        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData.get(columnName);
        }).when(resultSet).get(anyString());

        doAnswer(invocationOnMock -> resultSet)
                .when(kpaCatalogue)
                .getById(anyInt());

        var actual = this.dataMapper.getById(1);
        assertThat(actual).isInstanceOf(Patient.class);
        assertThat(actual.getId()).isEqualTo("1");
        assertThat(actual.getGender().getCode()).isEqualTo(GenderCodingCode.FEMALE);
        assertThat(actual.getBirthDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T12:00:00Z")));
        assertThat(actual.getDateOfDeath()).isNull();
        assertThat(actual.getHealthInsurance()).isEqualTo(
                HealthInsurance.builder().type(HealthInsuranceCoding.builder().code(HealthInsuranceCodingCode.PKV).build()).build()
        );
    }

}
