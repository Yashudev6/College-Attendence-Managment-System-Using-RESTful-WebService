package com.pillai.system.service;

import com.pillai.system.dto.AttendanceDto;
import com.pillai.system.dto.AttendanceRequestDto;
import com.pillai.system.dto.AttendanceSummaryDto;
import com.pillai.system.entity.Attendance;
import com.pillai.system.entity.AttendanceStatus;
import com.pillai.system.entity.Student;
import com.pillai.system.entity.Subject;
import com.pillai.system.exception.BadRequestException;
import com.pillai.system.exception.DuplicateResourceException;
import com.pillai.system.exception.ResourceNotFoundException;
import com.pillai.system.repository.AttendanceRepository;
import com.pillai.system.repository.StudentRepository;
import com.pillai.system.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public List<AttendanceDto> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceDto getAttendanceById(Long id) {
        Attendance att = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));
        return convertToDto(att);
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceBySubjectId(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Subject not found with id: " + subjectId);
        }
        return attendanceRepository.findBySubjectId(subjectId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceDto markAttendance(AttendanceRequestDto requestDto) {
        if (requestDto.getDate() == null) {
            throw new BadRequestException("Attendance date cannot be empty");
        }

        Student student = studentRepository.findById(requestDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + requestDto.getStudentId()));

        Subject subject = subjectRepository.findById(requestDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + requestDto.getSubjectId()));

        // Prevent duplicate attendance for same student, subject and date
        if (attendanceRepository.existsByStudentIdAndSubjectIdAndDate(requestDto.getStudentId(), requestDto.getSubjectId(), requestDto.getDate())) {
            throw new DuplicateResourceException(String.format("Attendance already marked for Student %s in Subject %s on date %s",
                    student.getStudentId(), subject.getSubjectCode(), requestDto.getDate()));
        }

        Attendance attendance = new Attendance(
                student,
                subject,
                requestDto.getDate(),
                requestDto.getStatus(),
                requestDto.getRemarks()
        );

        attendanceRepository.save(attendance);
        return convertToDto(attendance);
    }

    @Transactional
    public AttendanceDto updateAttendance(Long id, AttendanceRequestDto requestDto) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));

        Student student = studentRepository.findById(requestDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + requestDto.getStudentId()));

        Subject subject = subjectRepository.findById(requestDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + requestDto.getSubjectId()));

        // If date/student/subject changed, verify duplicate
        boolean isKeyChanged = !attendance.getStudent().getId().equals(requestDto.getStudentId()) ||
                !attendance.getSubject().getId().equals(requestDto.getSubjectId()) ||
                !attendance.getDate().equals(requestDto.getDate());

        if (isKeyChanged && attendanceRepository.existsByStudentIdAndSubjectIdAndDate(requestDto.getStudentId(), requestDto.getSubjectId(), requestDto.getDate())) {
            throw new DuplicateResourceException(String.format("Attendance already marked for Student %s in Subject %s on date %s",
                    student.getStudentId(), subject.getSubjectCode(), requestDto.getDate()));
        }

        attendance.setStudent(student);
        attendance.setSubject(subject);
        attendance.setDate(requestDto.getDate());
        attendance.setStatus(requestDto.getStatus());
        attendance.setRemarks(requestDto.getRemarks());

        attendanceRepository.save(attendance);
        return convertToDto(attendance);
    }

    @Transactional
    public AttendanceDto patchAttendance(Long id, AttendanceStatus status, String remarks) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));

        if (status != null) {
            attendance.setStatus(status);
        }
        if (remarks != null) {
            attendance.setRemarks(remarks);
        }

        attendanceRepository.save(attendance);
        return convertToDto(attendance);
    }

    @Transactional
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance record not found with id: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    /**
     * Automatic Attendance Percentage Calculation Formula:
     * Attendance Percentage = (Present / Total) * 100
     */
    @Transactional(readOnly = true)
    public AttendanceSummaryDto calculateAttendanceSummary(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        long totalClasses = attendanceRepository.countByStudentId(studentId);
        long presentCount = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT);
        long absentCount = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.ABSENT);
        long lateCount = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.LATE);

        // Treat Present and Late as attended if applicable, or strictly Present. Standard: Present / Total
        double percentage = totalClasses > 0 ? ((double) (presentCount + lateCount) / totalClasses) * 100.0 : 0.0;
        percentage = Math.round(percentage * 100.0) / 100.0; // Round to 2 decimal places

        boolean lowAttendance = totalClasses > 0 && percentage < 75.0;

        String className = student.getSchoolClass() != null ? student.getSchoolClass().getClassName() : "N/A";

        return new AttendanceSummaryDto(
                student.getId(),
                student.getStudentId(),
                student.getName(),
                className,
                totalClasses,
                presentCount,
                absentCount,
                lateCount,
                percentage,
                lowAttendance
        );
    }

    @Transactional(readOnly = true)
    public List<AttendanceSummaryDto> getLowAttendanceStudents() {
        List<Student> students = studentRepository.findAll();
        List<AttendanceSummaryDto> lowAttendanceList = new ArrayList<>();

        for (Student s : students) {
            AttendanceSummaryDto summary = calculateAttendanceSummary(s.getId());
            if (summary.isLowAttendance()) {
                lowAttendanceList.add(summary);
            }
        }
        return lowAttendanceList;
    }

    public AttendanceDto convertToDto(Attendance att) {
        return new AttendanceDto(
                att.getId(),
                att.getStudent().getId(),
                att.getStudent().getStudentId(),
                att.getStudent().getName(),
                att.getSubject().getId(),
                att.getSubject().getSubjectCode(),
                att.getSubject().getSubjectName(),
                att.getDate(),
                att.getStatus(),
                att.getRemarks()
        );
    }
}
