package com.pillai.system.controller;

import com.pillai.system.dto.ApiResponse;
import com.pillai.system.dto.DepartmentDto;
import com.pillai.system.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved successfully", departments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDto>> getDepartmentById(@PathVariable("id") Long id) {
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Department retrieved successfully", department));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDto>> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto created = departmentService.createDepartment(departmentDto);
        return new ResponseEntity<>(ApiResponse.success("Department created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDto>> updateDepartment(@PathVariable("id") Long id, @Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto updated = departmentService.updateDepartment(id, departmentDto);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully", null));
    }
}
