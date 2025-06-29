package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.RequestedMedicationSystem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonToMedicationMapperTest {

    // See example in: https://github.com/pcvolkmer/onkostar-plugin-dnpm/blob/master/sql/sql-queries.md
    @Test
    void shouldMapTherapielinieJson() {
        var json = "[\n" +
                "    {\"system\":\"other\",\"code\":\"Gemcitabin\",\"substance\":\"Gemcitabin (dFdC)\"},\n" +
                "    {\"system\":\"other\",\"code\":\"Cisplatin\",\"substance\":\"Cisplatin (CDDP)\"}\n" +
                "]";

        var actual = JsonToMedicationMapper.map(json);

        assertThat(actual).hasSize(2);

        var elem0 = actual.get(0);
        assertThat(elem0.getCode()).isEqualTo("Gemcitabin");
        assertThat(elem0.getDisplay()).isEqualTo("Gemcitabin (dFdC)");
        assertThat(elem0.getSystem()).isEqualTo(RequestedMedicationSystem.UNDEFINED);
        assertThat(elem0.getVersion()).isNull();

        var elem1 = actual.get(1);
        assertThat(elem1.getCode()).isEqualTo("Cisplatin");
        assertThat(elem1.getDisplay()).isEqualTo("Cisplatin (CDDP)");
        assertThat(elem0.getSystem()).isEqualTo(RequestedMedicationSystem.UNDEFINED);
        assertThat(elem0.getVersion()).isNull();
    }

    // See example in: https://github.com/pcvolkmer/onkostar-plugin-dnpm/blob/master/sql/sql-queries.md
    @Test
    void shouldMapEinzelempfehlungJson() {
        var json = "[\n" +
                "    {\"code\":\"\",\"name\":\"PARP-Inhibierung\",\"system\":\"UNREGISTERED\"}\n" +
                "]";

        var actual = JsonToMedicationMapper.map(json);

        assertThat(actual).hasSize(1);

        var elem0 = actual.get(0);
        assertThat(elem0.getCode()).isEmpty();
        assertThat(elem0.getDisplay()).isEqualTo("PARP-Inhibierung");
        assertThat(elem0.getSystem()).isEqualTo(RequestedMedicationSystem.UNDEFINED);
        assertThat(elem0.getVersion()).isNull();
    }

}
