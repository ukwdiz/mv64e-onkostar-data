package dev.pcvolkmer.onco.datamapper;

import dev.pcvolkmer.mv64e.mtb.Mtb;
import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class MtbDataMapperTest {

    JdbcTemplate jdbcTemplate;

    MtbDataMapper mtbDataMapper;

    @BeforeEach
    void setUp(@Mock JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.mtbDataMapper = new MtbDataMapper(jdbcTemplate);
    }

    @Test
    void shouldCreateDataMapper(@Mock DataSource dataSource) {
        assertThat(MtbDataMapper.create(dataSource)).isNotNull();
    }

    @Test
    void shouldUseKpaProcedureId() {
        doAnswer(invocationOnMock -> List.of(1))
                .when(jdbcTemplate)
                .query(anyString(), any(RowMapper.class), anyString());

        var actual = this.mtbDataMapper.getByCaseId("16000123");
        assertThat(actual).isInstanceOf(Mtb.class);
    }

    @Test
    void shouldThrowExceptionIfNoKpaProcedureFound() {
        var ex = assertThrows(DataAccessException.class, () -> {
            this.mtbDataMapper.getByCaseId("16000123");
        });
        assertThat(ex).hasMessage("No record found for case: 16000123");
    }

    @Test
    void shouldThrowExceptionIfMultipleKpaProceduresFound() {
        doAnswer(invocationOnMock -> List.of(1, 2))
                .when(jdbcTemplate)
                .query(anyString(), any(RowMapper.class), anyString());

        var ex = assertThrows(DataAccessException.class, () -> {
            this.mtbDataMapper.getByCaseId("16000123");
        });
        assertThat(ex).hasMessage("Multiple records found for case: 16000123");
    }

}
