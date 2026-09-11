package com.kingoftheserver.plugin.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DurationUtilTest {

    @Test
    void parsesPlainSeconds() {
        assertEquals(Duration.ofSeconds(45), DurationUtil.parse("45"));
    }

    @Test
    void parsesMinutes() {
        assertEquals(Duration.ofMinutes(7), DurationUtil.parse("7m"));
    }

    @Test
    void parsesCombinedComponents() {
        assertEquals(Duration.ofHours(1).plusMinutes(30).plusSeconds(20), DurationUtil.parse("1h30m20s"));
    }

    @Test
    void parsesDays() {
        assertEquals(Duration.ofDays(2), DurationUtil.parse("2d"));
    }

    @Test
    void isCaseInsensitive() {
        assertEquals(Duration.ofMinutes(7), DurationUtil.parse("7M"));
    }

    @Test
    void rejectsBlankString() {
        assertThrows(IllegalArgumentException.class, () -> DurationUtil.parse(""));
    }

    @Test
    void rejectsGarbage() {
        assertThrows(IllegalArgumentException.class, () -> DurationUtil.parse("banana"));
    }

    @Test
    void rejectsPartiallyValidString() {
        assertThrows(IllegalArgumentException.class, () -> DurationUtil.parse("7mxyz"));
    }

    @Test
    void rejectsZeroDuration() {
        assertThrows(IllegalArgumentException.class, () -> DurationUtil.parse("0s"));
    }

    @Test
    void formatsClockUnderAMinute() {
        assertEquals("00:09", DurationUtil.formatClock(Duration.ofSeconds(9)));
    }

    @Test
    void formatsClockOverAMinute() {
        assertEquals("07:00", DurationUtil.formatClock(Duration.ofMinutes(7)));
    }

    @Test
    void clampsNegativeDurationToZero() {
        assertEquals("00:00", DurationUtil.formatClock(Duration.ofSeconds(-5)));
    }
}
