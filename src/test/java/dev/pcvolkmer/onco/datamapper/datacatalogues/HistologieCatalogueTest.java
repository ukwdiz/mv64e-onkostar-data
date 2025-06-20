package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HistologieCatalogueTest {

    JdbcTemplate jdbcTemplate;
    HistologieCatalogue catalogue;

    @BeforeEach
    void setUp(@Mock JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.catalogue = HistologieCatalogue.create(jdbcTemplate);
    }

    @Test
    void shouldUseCorrectQuery(@Mock ResultSet resultSet) {
        doAnswer(invocationOnMock -> List.of(resultSet))
                .when(jdbcTemplate)
                .query(anyString(), any(RowMapper.class), anyInt());

        this.catalogue.getById(1);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(this.jdbcTemplate).query(captor.capture(), any(RowMapper.class), anyInt());

        assertThat(captor.getValue())
                .isEqualTo("SELECT * FROM dk_dnpm_uf_histologie JOIN prozedur ON (prozedur.id = dk_dnpm_uf_histologie.id) WHERE id = ?");
    }

}
