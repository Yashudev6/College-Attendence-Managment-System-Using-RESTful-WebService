package com.pillai.system.controller;

import com.pillai.system.dto.ApiResponse;
import com.pillai.system.dto.StudentDto;
import com.pillai.system.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentDto>>> getAllStudents(
            @RequestParam(value = "search", required = false) String search) {
        List<StudentDto> students;
        if (search != null && !search.trim().isEmpty()) {
            students = studentService.searchStudents(search);
        } else {
            students = studentService.getAllStudents();
        }
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDto>> getStudentById(@PathVariable("id") Long id) {
        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getStudentsByClassId(@PathVariable("classId") Long classId) {
        List<StudentDto> students = studentService.getStudentsByClassId(classId);
        return ResponseEntity.ok(ApiResponse.success("Class students retrieved successfully", students));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StudentDto>> getMyProfile(Authentication authentication) {
        String username = authentication.getName();
        StudentDto student = studentService.getStudentByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", student));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StudentDto>> createStudent(@Valid @RequestBody StudentDto studentDto) {
        StudentDto created = studentService.createStudent(studentDto);
        return new ResponseEntity<>(ApiResponse.success("Student created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDto>> updateStudent(@PathVariable("id") Long id, @Valid @RequestBody StudentDto studentDto) {
        StudentDto updated = studentService.updateStudent(id, studentDto);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", updated));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDto>> patchStudent(@PathVariable("id") Long id, @RequestBody StudentDto studentDto) {
        StudentDto patched = studentService.patchStudent(id, studentDto);
        return ResponseEntity.ok(ApiResponse.success("Student patched successfully", patched));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable("id") Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }
}
