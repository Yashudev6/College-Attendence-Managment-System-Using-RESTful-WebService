package com.pillai.system.controller;

import com.pillai.system.dto.ApiResponse;
import com.pillai.system.dto.TeacherDto;
import com.pillai.system.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherDto>>> getAllTeachers() {
        List<TeacherDto> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(ApiResponse.success("Teachers retrieved successfully", teachers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherDto>> getTeacherById(@PathVariable("id") Long id) {
        TeacherDto teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(ApiResponse.success("Teacher retrieved successfully", teacher));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TeacherDto>> createTeacher(@Valid @RequestBody TeacherDto teacherDto) {
        TeacherDto created = teacherService.createTeacher(teacherDto);
        return new ResponseEntity<>(ApiResponse.success("Teacher created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherDto>> updateTeacher(@PathVariable("id") Long id, @Valid @RequestBody TeacherDto teacherDto) {
        TeacherDto updated = teacherService.updateTeacher(id, teacherDto);
        return ResponseEntity.ok(ApiResponse.success("Teacher updated successfully", updated));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherDto>> patchTeacher(@PathVariable("id") Long id, @RequestBody TeacherDto teacherDto) {
        TeacherDto patched = teacherService.patchTeacher(id, teacherDto);
        return ResponseEntity.ok(ApiResponse.success("Teacher patched successfully", patched));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(@PathVariable("id") Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok(ApiResponse.success("Teacher deleted successfully", null));
    }
}
