package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.mv64e.mtb.StudySystem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonToStudyMapperTest {

    // See example in: https://github.com/pcvolkmer/onkostar-plugin-dnpm/blob/master/sql/sql-queries.md
    @Test
    void shouldMapEinzelempfehlungJson() {
        var json = "[\n" +
                "    {\"studie\":\"TestInhibitor\",\"system\":\"NCT\",\"id\":\"NCT12345678\",\"nct\":\"NCT12345678\",\"ort\":\"Teststadt\",\"internextern\":\"e\"}\n" +
                "]";

        var actual = JsonToStudyMapper.map(json);

        assertThat(actual).hasSize(1);

        var study = actual.get(0);
        assertThat(study.getId()).isEqualTo("NCT12345678");
        assertThat(study.getDisplay()).isNull();
        assertThat(study.getSystem()).isEqualTo(StudySystem.NCT);
        assertThat(study.getType()).isEqualTo("Study");
    }

}
