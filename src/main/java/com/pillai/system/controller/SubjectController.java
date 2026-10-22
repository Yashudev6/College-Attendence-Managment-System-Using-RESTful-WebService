package com.pillai.system.controller;

import com.pillai.system.dto.ApiResponse;
import com.pillai.system.dto.SubjectDto;
import com.pillai.system.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getAllSubjects() {
        List<SubjectDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved successfully", subjects));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectDto>> getSubjectById(@PathVariable("id") Long id) {
        SubjectDto subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(ApiResponse.success("Subject retrieved successfully", subject));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getSubjectsByClassId(@PathVariable("classId") Long classId) {
        List<SubjectDto> subjects = subjectService.getSubjectsByClassId(classId);
        return ResponseEntity.ok(ApiResponse.success("Class subjects retrieved successfully", subjects));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getSubjectsByTeacherId(@PathVariable("teacherId") Long teacherId) {
        List<SubjectDto> subjects = subjectService.getSubjectsByTeacherId(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Teacher subjects retrieved successfully", subjects));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectDto>> createSubject(@Valid @RequestBody SubjectDto subjectDto) {
        SubjectDto created = subjectService.createSubject(subjectDto);
        return new ResponseEntity<>(ApiResponse.success("Subject created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectDto>> updateSubject(@PathVariable("id") Long id, @Valid @RequestBody SubjectDto subjectDto) {
        SubjectDto updated = subjectService.updateSubject(id, subjectDto);
        return ResponseEntity.ok(ApiResponse.success("Subject updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable("id") Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully", null));
    }
}
