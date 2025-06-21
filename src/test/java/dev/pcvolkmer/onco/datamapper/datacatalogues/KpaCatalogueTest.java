package dev.pcvolkmer.onco.datamapper.datacatalogues;

import dev.pcvolkmer.onco.datamapper.exceptions.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KpaCatalogueTest {

    JdbcTemplate jdbcTemplate;
    KpaCatalogue catalogue;

    @BeforeEach
    void setUp(@Mock JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.catalogue = KpaCatalogue.create(jdbcTemplate);
    }

    @Test
    void shouldUseCorrectQuery(@Mock Map<String, Object> resultSet) {
        doAnswer(invocationOnMock -> List.of(resultSet))
                .when(jdbcTemplate)
                .queryForList(anyString(), anyInt());

        this.catalogue.getById(1);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(this.jdbcTemplate).queryForList(captor.capture(), anyInt());

        assertThat(captor.getValue())
                .isEqualTo("SELECT * FROM dk_dnpm_kpa JOIN prozedur ON (prozedur.id = dk_dnpm_kpa.id) WHERE geloescht = 0 AND prozedur.id = ?");
    }

    @Test
    void shouldThrowExceptionIfNoKpaProcedureFound() {
        doAnswer(invocationOnMock -> List.of())
                .when(jdbcTemplate).query(anyString(), any(RowMapper.class), anyString());

        var ex = assertThrows(DataAccessException.class, () -> catalogue.getProcedureIdByCaseId("16000123"));
        assertThat(ex).hasMessage("No record found for case: 16000123");
    }

}
