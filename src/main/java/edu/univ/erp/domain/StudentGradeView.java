package edu.univ.erp.domain;

public class StudentGradeView {
    private final String courseCode;
    private final String courseTitle; // <--- NEW FIELD
    private final String component;
    private final double score;
    private final int maxScore;
    private final int weight;         // <--- NEW FIELD (Need this to calculate final grade)

    public StudentGradeView(String courseCode, String courseTitle, String component, double score, int maxScore, int weight) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.component = component;
        this.score = score;
        this.maxScore = maxScore;
        this.weight = weight;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getComponent() { return component; }
    public double getScore() { return score; }
    public int getMaxScore() { return maxScore; }
    public int getWeight() { return weight; }
}