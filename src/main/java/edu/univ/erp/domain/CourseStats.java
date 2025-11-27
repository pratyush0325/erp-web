package edu.univ.erp.domain;

public class CourseStats {
    private final String courseCode;
    private final String courseTitle;
    private final int totalEnrolled;
    private final int gradesSubmitted; // Count of students with at least some marks
    private final double minScore;
    private final double maxScore;
    private final double avgScore;

    public CourseStats(String courseCode, String courseTitle, int totalEnrolled, int gradesSubmitted, double minScore, double maxScore, double avgScore) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.totalEnrolled = totalEnrolled;
        this.gradesSubmitted = gradesSubmitted;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.avgScore = avgScore;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getTotalEnrolled() { return totalEnrolled; }
    public int getGradesSubmitted() { return gradesSubmitted; }
    public double getMinScore() { return minScore; }
    public double getMaxScore() { return maxScore; }
    public double getAvgScore() { return avgScore; }
}