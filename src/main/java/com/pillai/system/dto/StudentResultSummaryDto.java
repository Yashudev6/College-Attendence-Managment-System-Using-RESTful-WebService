package com.pillai.system.dto;

import java.util.List;

public class StudentResultSummaryDto {
    private Long studentId;
    private String studentNumber;
    private String studentName;
    private String className;
    private Double totalMarks;
    private Double totalMaxMarks;
    private Double overallPercentage;
    private String overallGrade;
    private List<ResultDto> results;

    public StudentResultSummaryDto() {
    }

    public StudentResultSummaryDto(Long studentId, String studentNumber, String studentName, String className, Double totalMarks, Double totalMaxMarks, Double overallPercentage, String overallGrade, List<ResultDto> results) {
        this.studentId = studentId;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.className = className;
        this.totalMarks = totalMarks;
        this.totalMaxMarks = totalMaxMarks;
        this.overallPercentage = overallPercentage;
        this.overallGrade = overallGrade;
        this.results = results;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Double getTotalMaxMarks() {
        return totalMaxMarks;
    }

    public void setTotalMaxMarks(Double totalMaxMarks) {
        this.totalMaxMarks = totalMaxMarks;
    }

    public Double getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(Double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public String getOverallGrade() {
        return overallGrade;
    }

    public void setOverallGrade(String overallGrade) {
        this.overallGrade = overallGrade;
    }

    public List<ResultDto> getResults() {
        return results;
    }

    public void setResults(List<ResultDto> results) {
        this.results = results;
    }
}
