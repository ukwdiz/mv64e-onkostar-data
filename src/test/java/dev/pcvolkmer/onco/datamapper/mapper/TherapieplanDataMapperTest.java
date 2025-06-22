package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.MtbCarePlan;
import dev.pcvolkmer.mv64e.mtb.Reference;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.TherapieplanCatalogue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class TherapieplanDataMapperTest {

    TherapieplanCatalogue therapieplanCatalogue;
    PropertyCatalogue propertyCatalogue;

    TherapieplanDataMapper dataMapper;

    @BeforeEach
    void setUp(
            @Mock TherapieplanCatalogue therapieplanCatalogue,
            @Mock PropertyCatalogue propertyCatalogue
    ) {
        this.therapieplanCatalogue = therapieplanCatalogue;
        this.propertyCatalogue = propertyCatalogue;
        this.dataMapper = new TherapieplanDataMapper(therapieplanCatalogue, propertyCatalogue);
    }

    @Test
    void shouldCreateCarePlan(@Mock ResultSet resultSet) {
        final var testData = Map.of(
                "id", "1",
                "patient_id", "42"
        );

        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            return testData.get(columnName);
        }).when(resultSet).getString(anyString());

        doAnswer(invocationOnMock -> resultSet)
                .when(therapieplanCatalogue)
                .getById(anyInt());

        var actual = this.dataMapper.getById(1);
        assertThat(actual).isInstanceOf(MtbCarePlan.class);
        assertThat(actual.getId()).isEqualTo("1");
        assertThat(actual.getPatient())
                .isEqualTo(Reference.builder().id("42").type("Patient").build());
    }

}
