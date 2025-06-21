package dev.pcvolkmer.onco.datamapper;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.sql.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResultSetTest {

    @Test
    void shouldReturnStringValues() {
        var data = getTestData();

        assertThat(data.getString("null")).isNull();
        assertThat(data.getString("string")).isEqualTo("TestString");
        assertThat(data.getString("int")).isEqualTo("42");
    }

    @Test
    void shouldReturnIntegerValues() {
        var data = getTestData();

        assertThat(data.getInteger("int")).isEqualTo(42);
    }

    @Test
    void shouldReturnLongValues() {
        var data = getTestData();

        assertThat(data.getLong("int")).isEqualTo(42L);
    }

    @Test
    void shouldReturnDateValues() {
        var data = getTestData();

        assertThat(data.getDate("date")).isEqualTo(new Date(Date.from(Instant.parse("2025-06-21T12:00:00Z")).getTime()));
    }

    static ResultSet getTestData() {
        return ResultSet.from(
                Map.of(
                        "string", "TestString",
                        "int", 42,
                        "date", new Date(Date.from(Instant.parse("2025-06-21T12:00:00Z")).getTime())
                )
        );
    }

}
