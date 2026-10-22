package com.pillai.system.service;

import com.pillai.system.dto.AttendanceSummaryDto;
import com.pillai.system.dto.DashboardStatsDto;
import com.pillai.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private AttendanceService attendanceService;

    public DashboardStatsDto getDashboardStats() {
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalSubjects = subjectRepository.count();
        long totalClasses = classRepository.count();
        long totalAttendanceRecords = attendanceRepository.count();
        long totalResults = resultRepository.count();

        List<AttendanceSummaryDto> lowAttendanceStudents = attendanceService.getLowAttendanceStudents();
        long lowAttendanceCount = lowAttendanceStudents.size();

        List<String> recentActivities = new ArrayList<>();
        recentActivities.add("System initialized and seed database loaded.");
        recentActivities.add(totalStudents + " active student profiles in database.");
        recentActivities.add(totalTeachers + " faculty members assigned to departments.");
        recentActivities.add(totalAttendanceRecords + " attendance transactions recorded to date.");
        recentActivities.add(totalResults + " examination mark sheets generated.");

        return new DashboardStatsDto(
                totalStudents,
                totalTeachers,
                totalSubjects,
                totalClasses,
                totalAttendanceRecords,
                totalResults,
                lowAttendanceCount,
                lowAttendanceStudents,
                recentActivities
        );
    }
}
