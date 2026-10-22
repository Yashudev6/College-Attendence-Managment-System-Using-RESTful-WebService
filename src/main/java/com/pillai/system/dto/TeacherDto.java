package com.pillai.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class TeacherDto {
    private Long id;

    @NotBlank(message = "Teacher ID is required")
    private String teacherId;

    @NotBlank(message = "Teacher name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    private String phone;
    private Long departmentId;
    private String departmentName;
    private Long userId;

    public TeacherDto() {
    }

    public TeacherDto(Long id, String teacherId, String name, String email, String phone, Long departmentId, String departmentName, Long userId) {
        this.id = id;
        this.teacherId = teacherId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
