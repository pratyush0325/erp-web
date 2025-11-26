package edu.univ.erp.domain;

public class AssignmentScore {
    private final int assignmentId;
    private final String assignmentName;
    private final int maxScore;
    private final int weight; // <-- Ensure this field exists
    private final Double studentScore;

    public AssignmentScore(int assignmentId, String assignmentName, int maxScore, int weight, Double studentScore) {
        this.assignmentId = assignmentId;
        this.assignmentName = assignmentName;
        this.maxScore = maxScore;
        this.weight = weight;
        this.studentScore = studentScore;
    }

    public int getAssignmentId() { return assignmentId; }
    public String getAssignmentName() { return assignmentName; }
    public int getMaxScore() { return maxScore; }
    public int getWeight() { return weight; }
    public Double getStudentScore() { return studentScore; }
}