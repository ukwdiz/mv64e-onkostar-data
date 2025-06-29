package dev.pcvolkmer.onco.datamapper;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertThat(data.getDate("date")).isEqualTo(new Date(Date.from(Instant.parse("2025-06-21T00:00:00Z")).getTime()));
    }

    @Test
    void shouldHandleBooleanValues() {
        var data = getTestData();

        assertTrue(data.isTrue("true"));
        assertFalse(data.isTrue("false"));
    }

    static ResultSet getTestData() {
        return ResultSet.from(
                Map.of(
                        "string", "TestString",
                        "int", 42,
                        "date", new Date(Date.from(Instant.parse("2025-06-21T02:00:00Z")).getTime()),
                        "true", 1,
                        "false", 0
                )
        );
    }

}
