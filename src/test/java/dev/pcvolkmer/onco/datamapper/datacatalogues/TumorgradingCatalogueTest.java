package dev.pcvolkmer.onco.datamapper.datacatalogues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TumorgradingCatalogueTest {

    JdbcTemplate jdbcTemplate;
    TumorgradingCatalogue catalogue;

    @BeforeEach
    void setUp(@Mock JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.catalogue = TumorgradingCatalogue.create(jdbcTemplate);
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
                .isEqualTo("SELECT * FROM dk_dnpm_uf_tumorgrading JOIN prozedur ON (prozedur.id = dk_dnpm_uf_tumorgrading.id) WHERE geloescht = 0 AND prozedur.id = ?");
    }

    @Test
    void shouldUseCorrectSubformQuery(@Mock Map<String, Object> resultSet) {
        doAnswer(invocationOnMock -> List.of(resultSet))
                .when(jdbcTemplate)
                .queryForList(anyString(), anyInt());

        this.catalogue.getAllByParentId(1);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(this.jdbcTemplate).queryForList(captor.capture(), anyInt());

        assertThat(captor.getValue())
                .isEqualTo("SELECT * FROM dk_dnpm_uf_tumorgrading JOIN prozedur ON (prozedur.id = dk_dnpm_uf_tumorgrading.id) WHERE geloescht = 0 AND hauptprozedur_id = ?");
    }

}
