package com.pillai.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClassDto {
    private Long id;

    @NotBlank(message = "Class name is required")
    private String className;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String departmentName;

    public ClassDto() {
    }

    public ClassDto(Long id, String className, String semester, Long departmentId, String departmentName) {
        this.id = id;
        this.className = className;
        this.semester = semester;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
