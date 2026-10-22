package com.pillai.system.controller;

import com.pillai.system.dto.ApiResponse;
import com.pillai.system.dto.ClassDto;
import com.pillai.system.service.ClassService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassDto>>> getAllClasses() {
        List<ClassDto> classes = classService.getAllClasses();
        return ResponseEntity.ok(ApiResponse.success("Classes retrieved successfully", classes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassDto>> getClassById(@PathVariable("id") Long id) {
        ClassDto schoolClass = classService.getClassById(id);
        return ResponseEntity.ok(ApiResponse.success("Class retrieved successfully", schoolClass));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClassDto>> createClass(@Valid @RequestBody ClassDto classDto) {
        ClassDto created = classService.createClass(classDto);
        return new ResponseEntity<>(ApiResponse.success("Class created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassDto>> updateClass(@PathVariable("id") Long id, @Valid @RequestBody ClassDto classDto) {
        ClassDto updated = classService.updateClass(id, classDto);
        return ResponseEntity.ok(ApiResponse.success("Class updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable("id") Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok(ApiResponse.success("Class deleted successfully", null));
    }
}
