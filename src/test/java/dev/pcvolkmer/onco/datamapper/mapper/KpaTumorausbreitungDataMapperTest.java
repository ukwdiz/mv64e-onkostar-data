package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.TumorStaging;
import dev.pcvolkmer.mv64e.mtb.TumorStagingMethodCoding;
import dev.pcvolkmer.mv64e.mtb.TumorStagingMethodCodingCode;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TumorausbreitungCatalogue;
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

@ExtendWith(MockitoExtension.class)
class KpaTumorausbreitungDataMapperTest {

    TumorausbreitungCatalogue catalogue;

    KpaTumorausbreitungDataMapper dataMapper;

    @BeforeEach
    void setUp(@Mock TumorausbreitungCatalogue catalogue) {
        this.catalogue = catalogue;
        this.dataMapper = new KpaTumorausbreitungDataMapper(catalogue);
    }

    @Test
    void shouldMapResultSet(@Mock ResultSet resultSet) {
        var testData = Map.of(
                "id", "1",
                "zeitpunkt", new java.sql.Date(Date.from(Instant.parse("2000-01-01T12:00:00Z")).getTime()),
                "typ", "pathologic",
                "wert", "tumor-free",
                "tnmtprefix", "p",
                "tnmt", "0",
                "tnmnprefix", "p",
                "tnmn", "0",
                "tnmmprefix", "p",
                "tnmm", "0"
        );

        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData.get(columnName);
        }).when(resultSet).getString(anyString());

        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData.get(columnName);
        }).when(resultSet).getDate(anyString());

        doAnswer(invocationOnMock -> List.of(resultSet))
                .when(catalogue)
                .getAllByParentId(anyInt());

        var actualList = this.dataMapper.getByParentId(1);
        assertThat(actualList).hasSize(1);

        var actual = actualList.get(0);
        assertThat(actual).isInstanceOf(TumorStaging.class);
        assertThat(actual.getDate()).isEqualTo(new java.sql.Date(Date.from(Instant.parse("2000-01-01T12:00:00Z")).getTime()));
        assertThat(actual.getMethod()).isEqualTo(TumorStagingMethodCoding.builder().code(TumorStagingMethodCodingCode.PATHOLOGIC).build());
        assertThat(actual.getOtherClassifications()).hasSize(1);
        assertThat(actual.getOtherClassifications().get(0).getCode()).isEqualTo("tumor-free");
        assertThat(actual.getTnmClassification().getTumor().getCode()).isEqualTo("p0");
        assertThat(actual.getTnmClassification().getNodes().getCode()).isEqualTo("p0");
        assertThat(actual.getTnmClassification().getMetastasis().getCode()).isEqualTo("p0");
    }

}
