package edu.univ.erp.util;

public class GradeUtils {
    public static String getLetterGrade(double percentage) {
        if (percentage >= 90.0) return "A";
        if (percentage >= 80.0) return "A-";
        if (percentage >= 70.0) return "B";
        if (percentage >= 60.0) return "C";
        if (percentage >= 50.0) return "D";
        return "F";
    }
}