package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.MtbDiagnosis;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.KpaCatalogue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class KpaDiagnosisDataMapperTest {

    KpaCatalogue kpaCatalogue;

    KpaDiagnosisDataMapper dataMapper;

    @BeforeEach
    void setUp(@Mock KpaCatalogue kpaCatalogue) {
        this.kpaCatalogue = kpaCatalogue;
        this.dataMapper = new KpaDiagnosisDataMapper(kpaCatalogue);
    }

    @Test
    void shouldCreateDataMapper(@Mock DataSource dataSource) {
        assertThat(MtbDataMapper.create(dataSource)).isNotNull();
    }

    @Test
    void shouldCreateDiagnosis(@Mock ResultSet resultSet) {
        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData().get(columnName);
        }).when(resultSet).getString(anyString());

        doAnswer(invocationOnMock -> resultSet)
                .when(kpaCatalogue)
                .getById(anyInt());

        var actual = this.dataMapper.getById(1);
        assertThat(actual).isInstanceOf(MtbDiagnosis.class);
        assertThat(actual.getId()).isEqualTo("1");
        assertThat(actual.getCode().getCode()).isEqualTo("F79.9");
    }

    private static Map<String, Object> testData() {
        return Map.of(
                "id", "1",
                "icd10", "F79.9"
        );
    }

}
