package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.*;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.ProzedurCatalogue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KpaProzedurDataMapperTest {

    ProzedurCatalogue catalogue;

    KpaProzedurDataMapper dataMapper;

    @BeforeEach
    void setUp(@Mock ProzedurCatalogue catalogue) {
        this.catalogue = catalogue;
        this.dataMapper = new KpaProzedurDataMapper(catalogue);
    }

    @Test
    void shouldMapResultSet(@Mock ResultSet resultSet) {
        var testData = Map.of(
                "erkrankung.id", "1",
                "id", "1",
                "beginn", new java.sql.Date(Date.from(Instant.parse("2000-01-01T12:00:00Z")).getTime()),
                "ende", new java.sql.Date(Date.from(Instant.parse("2024-06-19T12:00:00Z")).getTime()),
                "erfassungsdatum", new java.sql.Date(Date.from(Instant.parse("2024-06-19T12:00:00Z")).getTime()),
                "intention", "S",
                "status", "stopped",
                "statusgrund", "patient-death",
                "therapielinie", 1L,
                "typ", "surgery"
        );

        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData.get(columnName);
        }).when(resultSet).getString(anyString());

        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData.get(columnName);
        }).when(resultSet).getLong(anyString());

        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData.get(columnName);
        }).when(resultSet).getDate(anyString());

        when(resultSet.getDiseaseId()).thenReturn(1);
        when(resultSet.getProcedureId()).thenReturn(1);

        doAnswer(invocationOnMock -> List.of(resultSet))
                .when(catalogue)
                .getAllByParentId(anyInt());

        doAnswer(invocationOnMock -> List.of(resultSet))
                .when(catalogue)
                .getDiseases(anyInt());

        var actualList = this.dataMapper.getByParentId(1);
        assertThat(actualList).hasSize(1);

        var actual = actualList.get(0);
        assertThat(actual).isInstanceOf(OncoProcedure.class);
        assertThat(actual.getId()).isEqualTo("1");
        assertThat(actual.getPeriod()).isEqualTo(
                PeriodDate.builder()
                        .start(Date.from(Instant.parse("2000-01-01T12:00:00Z")))
                        .end(Date.from(Instant.parse("2024-06-19T12:00:00Z")))
                        .build()
        );
        assertThat(actual.getRecordedOn()).isEqualTo(Date.from(Instant.parse("2024-06-19T12:00:00Z")));
        assertThat(actual.getIntent()).isEqualTo(
                MtbTherapyIntentCoding.builder().code(MtbTherapyIntentCodingCode.S).build()
        );
        assertThat(actual.getStatus()).isEqualTo(
                TherapyStatusCoding.builder().code(TherapyStatusCodingCode.STOPPED).build()
        );
        assertThat(actual.getStatusReason()).isEqualTo(
                MtbTherapyStatusReasonCoding.builder().code(MtbTherapyStatusReasonCodingCode.PATIENT_DEATH).build()
        );
        assertThat(actual.getTherapyLine()).isEqualTo(1);
        assertThat(actual.getCode()).isEqualTo(
                OncoProcedureCoding.builder().code(OncoProcedureCodingCode.SURGERY).build()
        );
    }

}
