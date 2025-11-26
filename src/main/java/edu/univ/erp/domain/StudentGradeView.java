package edu.univ.erp.domain;

public class StudentGradeView {
    private final String courseCode;
    private final String component; // e.g. "Midterm"
    private final double score;
    private final int maxScore;

    public StudentGradeView(String courseCode, String component, double score, int maxScore) {
        this.courseCode = courseCode;
        this.component = component;
        this.score = score;
        this.maxScore = maxScore;
    }

    public String getCourseCode() { return courseCode; }
    public String getComponent() { return component; }
    public double getScore() { return score; }
    public int getMaxScore() { return maxScore; }
}