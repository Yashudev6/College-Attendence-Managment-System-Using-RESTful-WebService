package com.pillai.system.dto;

public class ResultDto {
    private Long id;
    private Long studentId;
    private String studentNumber;
    private String studentName;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private Double marks;
    private Double maxMarks;
    private Double percentage;
    private String semester;
    private String examType;
    private String grade;
    private String remarks;

    public ResultDto() {
    }

    public ResultDto(Long id, Long studentId, String studentNumber, String studentName, Long subjectId, String subjectCode, String subjectName, Double marks, Double maxMarks, Double percentage, String semester, String examType, String grade, String remarks) {
        this.id = id;
        this.studentId = studentId;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.marks = marks;
        this.maxMarks = maxMarks;
        this.percentage = percentage;
        this.semester = semester;
        this.examType = examType;
        this.grade = grade;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
