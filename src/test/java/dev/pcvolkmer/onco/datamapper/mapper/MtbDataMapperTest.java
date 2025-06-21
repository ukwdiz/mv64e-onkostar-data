package dev.pcvolkmer.onco.datamapper.mapper;

import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void shouldThrowExceptionIfNoKpaProcedureFound() {
        var ex = assertThrows(DataAccessException.class, () -> this.mtbDataMapper.getByCaseId("16000123"));
        assertThat(ex).hasMessage("No record found for case: 16000123");
    }

}
