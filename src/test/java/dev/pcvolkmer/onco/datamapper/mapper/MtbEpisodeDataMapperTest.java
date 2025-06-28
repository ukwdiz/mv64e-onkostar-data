package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.MtbEpisodeOfCare;
import dev.pcvolkmer.mv64e.mtb.PeriodDate;
import dev.pcvolkmer.mv64e.mtb.Reference;
import dev.pcvolkmer.onco.datamapper.PropertyCatalogue;
import dev.pcvolkmer.onco.datamapper.ResultSet;
import dev.pcvolkmer.onco.datamapper.datacatalogues.KpaCatalogue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class MtbEpisodeDataMapperTest {

    KpaCatalogue kpaCatalogue;
    PropertyCatalogue propertyCatalogue;

    MtbEpisodeDataMapper dataMapper;

    @BeforeEach
    void setUp(
            @Mock KpaCatalogue kpaCatalogue,
            @Mock PropertyCatalogue propertyCatalogue
    ) {
        this.kpaCatalogue = kpaCatalogue;
        this.propertyCatalogue = propertyCatalogue;
        this.dataMapper = new MtbEpisodeDataMapper(kpaCatalogue, propertyCatalogue);
    }

    @Test
    void shouldMapData() {
        final Map<String, Object> kpaData = Map.of(
                "id", 4711,
                "patient_id", 42,
                "anmeldedatummtb", new java.sql.Date(Date.from(Instant.parse("2025-06-28T12:00:00Z")).getTime())
        );

        doAnswer(invocationOnMock -> ResultSet.from(kpaData))
                .when(kpaCatalogue)
                .getById(anyInt());

        var actual = this.dataMapper.getById(1);
        assertThat(actual).isInstanceOf(MtbEpisodeOfCare.class);
        assertThat(actual.getId()).isEqualTo("4711");
        assertThat(actual.getPatient())
                .isEqualTo(Reference.builder().id("42").type("Patient").build());

        assertThat(actual.getDiagnoses())
                .hasSize(1);
        assertThat(actual.getDiagnoses().get(0))
                .isEqualTo(Reference.builder().id("4711").type("Diagnose").build());

        assertThat(actual.getPeriod())
                .isEqualTo(
                        // UTC day start!
                        PeriodDate.builder().start(Date.from(Instant.parse("2025-06-28T00:00:00Z"))).build()
                );
    }

}
