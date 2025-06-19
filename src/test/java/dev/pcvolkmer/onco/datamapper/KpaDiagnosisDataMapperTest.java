package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.mv64e.mtb.MtbDiagnosis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class KpaDiagnosisDataMapperTest {

    JdbcTemplate jdbcTemplate;

    KpaDiagnosisDataMapper dataMapper;

    @BeforeEach
    void setUp(@Mock JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataMapper = new KpaDiagnosisDataMapper(jdbcTemplate);
    }

    @Test
    void shouldCreateDataMapper(@Mock DataSource dataSource) {
        assertThat(MtbDataMapper.create(dataSource)).isNotNull();
    }

    @Test
    void shouldUseKpaProcedureId(@Mock ResultSet resultSet) throws SQLException {
        doAnswer(invocationOnMock -> {
            var columnName = invocationOnMock.getArgument(0, String.class);
            switch (columnName) {
                case "id":
                    return "1";
                case "icd10":
                    return "F79.9";
                default:
                    return null;
            }
        }).when(resultSet).getString(anyString());

        doAnswer(invocationOnMock -> List.of(resultSet))
                .when(jdbcTemplate)
                .query(anyString(), any(RowMapper.class), anyInt());

        var actual = this.dataMapper.getById(1);
        assertThat(actual).isInstanceOf(MtbDiagnosis.class);
        assertThat(actual.getId()).isEqualTo("1");
        assertThat(actual.getCode().getCode()).isEqualTo("F79.9");
    }

}
