package com.pillai.system.dto;

import jakarta.validation.constraints.NotBlank;

public class DepartmentDto {
    private Long id;

    @NotBlank(message = "Department name is required")
    private String departmentName;

    private String description;

    public DepartmentDto() {
    }

    public DepartmentDto(Long id, String departmentName, String description) {
        this.id = id;
        this.departmentName = departmentName;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
