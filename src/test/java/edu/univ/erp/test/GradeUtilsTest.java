package edu.univ.erp.test;

import edu.univ.erp.util.GradeUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class GradeUtilsTest {

    @Test
    @DisplayName("Score 95 should return A")
    void testGradeA() {
        String result = GradeUtils.getLetterGrade(95.0);
        assertEquals("A", result, "95 should be A");
    }

    @ParameterizedTest
    @CsvSource({
            "90.0, A",
            "89.9, A-",
            "80.0, A-",
            "79.9, B",
            "50.0, D",
            "49.9, F",
            "0.0, F"
    })
    @DisplayName("Boundary Value Analysis for Grades")
    void testGradeBoundaries(double score, String expectedLetter) {
        assertEquals(expectedLetter, GradeUtils.getLetterGrade(score));
    }
}