package edu.univ.erp.domain;

public class StudentGradeItem {
    private final int studentId;
    private final String name; // We'll use username for now
    private final String grade;

    public StudentGradeItem(int studentId, String name, String grade) {
        this.studentId = studentId;
        this.name = name;
        this.grade = (grade == null) ? "N/A" : grade;
    }

    public int getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getGrade() { return grade; }
}