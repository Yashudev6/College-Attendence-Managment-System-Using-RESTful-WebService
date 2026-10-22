package com.pillai.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResultRequestDto {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Marks field is required")
    @Min(value = 0, message = "Marks cannot be negative")
    private Double marks;

    @NotNull(message = "Max marks field is required")
    @Min(value = 1, message = "Max marks must be greater than 0")
    private Double maxMarks;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotBlank(message = "Exam type is required")
    private String examType;

    private String remarks;

    public ResultRequestDto() {
    }

    public ResultRequestDto(Long studentId, Long subjectId, Double marks, Double maxMarks, String semester, String examType, String remarks) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.marks = marks;
        this.maxMarks = maxMarks;
        this.semester = semester;
        this.examType = examType;
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

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
