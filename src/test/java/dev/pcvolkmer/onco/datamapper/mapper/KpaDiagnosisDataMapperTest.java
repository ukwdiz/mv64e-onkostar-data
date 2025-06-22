package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.MtbDiagnosis;
import dev.pcvolkmer.mv64e.mtb.Reference;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.KpaCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TumorausbreitungCatalogue;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TumorgradingCatalogue;
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
    TumorausbreitungCatalogue tumorausbreitungCatalogue;
    TumorgradingCatalogue tumorgradingCatalogue;
    PropertyCatalogue propertyCatalogue;

    KpaDiagnosisDataMapper dataMapper;

    @BeforeEach
    void setUp(
            @Mock KpaCatalogue kpaCatalogue,
            @Mock TumorausbreitungCatalogue tumorausbreitungCatalogue,
            @Mock TumorgradingCatalogue tumorgradingCatalogue,
            @Mock PropertyCatalogue propertyCatalogue
    ) {
        this.kpaCatalogue = kpaCatalogue;
        this.tumorausbreitungCatalogue = tumorausbreitungCatalogue;
        this.tumorgradingCatalogue = tumorgradingCatalogue;
        this.propertyCatalogue = propertyCatalogue;
        this.dataMapper = new KpaDiagnosisDataMapper(kpaCatalogue, tumorausbreitungCatalogue, tumorgradingCatalogue, propertyCatalogue);
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

        doAnswer(invocationOnMock ->
                new PropertyCatalogue.Entry("C00.0", "Bösartige Neubildung: Äußere Oberlippe", "Bösartige Neubildung: Äußere Oberlippe")
        ).when(propertyCatalogue).getByCodeAndVersion(anyString(), anyInt());

        var actual = this.dataMapper.getById(1);
        assertThat(actual).isInstanceOf(MtbDiagnosis.class);
        assertThat(actual.getId()).isEqualTo("1");
        assertThat(actual.getPatient())
                .isEqualTo(Reference.builder().id("42").type("Patient").build());
        assertThat(actual.getCode().getCode()).isEqualTo("F79.9");
    }

    private static Map<String, Object> testData() {
        return Map.of(
                "id", "1",
                "icd10", "F79.9",
                "patient_id", "42"
        );
    }

}
