package com.pillai.system.controller;

import com.pillai.system.dto.ApiResponse;
import com.pillai.system.dto.AttendanceDto;
import com.pillai.system.dto.AttendanceRequestDto;
import com.pillai.system.dto.AttendanceSummaryDto;
import com.pillai.system.entity.AttendanceStatus;
import com.pillai.system.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getAllAttendance() {
        List<AttendanceDto> attendanceList = attendanceService.getAllAttendance();
        return ResponseEntity.ok(ApiResponse.success("Attendance records retrieved successfully", attendanceList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceDto>> getAttendanceById(@PathVariable("id") Long id) {
        AttendanceDto attendance = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance record retrieved successfully", attendance));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getAttendanceByStudentId(@PathVariable("studentId") Long studentId) {
        List<AttendanceDto> attendanceList = attendanceService.getAttendanceByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student attendance records retrieved successfully", attendanceList));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getAttendanceBySubjectId(@PathVariable("subjectId") Long subjectId) {
        List<AttendanceDto> attendanceList = attendanceService.getAttendanceBySubjectId(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Subject attendance records retrieved successfully", attendanceList));
    }

    @GetMapping("/summary/student/{studentId}")
    public ResponseEntity<ApiResponse<AttendanceSummaryDto>> getStudentAttendanceSummary(@PathVariable("studentId") Long studentId) {
        AttendanceSummaryDto summary = attendanceService.calculateAttendanceSummary(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student attendance summary calculated", summary));
    }

    @GetMapping("/low-attendance")
    public ResponseEntity<ApiResponse<List<AttendanceSummaryDto>>> getLowAttendanceStudents() {
        List<AttendanceSummaryDto> lowList = attendanceService.getLowAttendanceStudents();
        return ResponseEntity.ok(ApiResponse.success("Low attendance students list (<75%) retrieved", lowList));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceDto>> markAttendance(@Valid @RequestBody AttendanceRequestDto requestDto) {
        AttendanceDto created = attendanceService.markAttendance(requestDto);
        return new ResponseEntity<>(ApiResponse.success("Attendance marked successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceDto>> updateAttendance(
            @PathVariable("id") Long id,
            @Valid @RequestBody AttendanceRequestDto requestDto) {
        AttendanceDto updated = attendanceService.updateAttendance(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Attendance record updated successfully", updated));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceDto>> patchAttendance(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> updates) {
        AttendanceStatus status = null;
        if (updates.containsKey("status") && updates.get("status") != null) {
            status = AttendanceStatus.valueOf(updates.get("status").toString().toUpperCase());
        }
        String remarks = updates.containsKey("remarks") ? (String) updates.get("remarks") : null;

        AttendanceDto patched = attendanceService.patchAttendance(id, status, remarks);
        return ResponseEntity.ok(ApiResponse.success("Attendance status patched successfully", patched));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable("id") Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance record deleted successfully", null));
    }
}
