package com.pillai.system.controller;

import com.pillai.system.dto.ApiResponse;
import com.pillai.system.dto.ResultDto;
import com.pillai.system.dto.ResultRequestDto;
import com.pillai.system.dto.StudentResultSummaryDto;
import com.pillai.system.service.ResultService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResultDto>>> getAllResults() {
        List<ResultDto> results = resultService.getAllResults();
        return ResponseEntity.ok(ApiResponse.success("Result records retrieved successfully", results));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResultDto>> getResultById(@PathVariable("id") Long id) {
        ResultDto result = resultService.getResultById(id);
        return ResponseEntity.ok(ApiResponse.success("Result record retrieved successfully", result));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<ResultDto>>> getResultsByStudentId(@PathVariable("studentId") Long studentId) {
        List<ResultDto> results = resultService.getResultsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student result records retrieved successfully", results));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<ResultDto>>> getResultsBySubjectId(@PathVariable("subjectId") Long subjectId) {
        List<ResultDto> results = resultService.getResultsBySubjectId(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Subject result records retrieved successfully", results));
    }

    @GetMapping("/summary/student/{studentId}")
    public ResponseEntity<ApiResponse<StudentResultSummaryDto>> getStudentResultSummary(@PathVariable("studentId") Long studentId) {
        StudentResultSummaryDto summary = resultService.calculateStudentResultSummary(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student overall result summary calculated", summary));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ResultDto>> enterMarks(@Valid @RequestBody ResultRequestDto requestDto) {
        ResultDto created = resultService.enterMarks(requestDto);
        return new ResponseEntity<>(ApiResponse.success("Student marks entered successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResultDto>> updateResult(
            @PathVariable("id") Long id,
            @Valid @RequestBody ResultRequestDto requestDto) {
        ResultDto updated = resultService.updateResult(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Result record updated successfully", updated));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ResultDto>> patchResult(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {
        Double marks = updates.containsKey("marks") && updates.get("marks") != null ? Double.valueOf(updates.get("marks").toString()) : null;
        Double maxMarks = updates.containsKey("maxMarks") && updates.get("maxMarks") != null ? Double.valueOf(updates.get("maxMarks").toString()) : null;
        String remarks = updates.containsKey("remarks") ? (String) updates.get("remarks") : null;

        ResultDto patched = resultService.patchResult(id, marks, maxMarks, remarks);
        return ResponseEntity.ok(ApiResponse.success("Result marks patched successfully", patched));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResult(@PathVariable("id") Long id) {
        resultService.deleteResult(id);
        return ResponseEntity.ok(ApiResponse.success("Result record deleted successfully", null));
    }
}
