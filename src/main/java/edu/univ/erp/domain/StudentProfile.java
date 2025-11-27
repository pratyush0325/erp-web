package edu.univ.erp.domain;

public class StudentProfile {
    private final String name;
    private final String rollNo;
    private final String program;
    private final int year;

    public StudentProfile(String name, String rollNo, String program, int year) {
        this.name = name;
        this.rollNo = rollNo;
        this.program = program;
        this.year = year;
    }

    public String getName() { return name; }
    public String getRollNo() { return rollNo; }
    public String getProgram() { return program; }
    public int getYear() { return year; }
}