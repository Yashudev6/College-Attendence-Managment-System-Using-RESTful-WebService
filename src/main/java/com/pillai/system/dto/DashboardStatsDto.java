package com.pillai.system.dto;

import java.util.List;

public class DashboardStatsDto {
    private long totalStudents;
    private long totalTeachers;
    private long totalSubjects;
    private long totalClasses;
    private long totalAttendanceRecords;
    private long totalResults;
    private long lowAttendanceCount;
    private List<AttendanceSummaryDto> lowAttendanceStudents;
    private List<String> recentActivities;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalStudents, long totalTeachers, long totalSubjects, long totalClasses, long totalAttendanceRecords, long totalResults, long lowAttendanceCount, List<AttendanceSummaryDto> lowAttendanceStudents, List<String> recentActivities) {
        this.totalStudents = totalStudents;
        this.totalTeachers = totalTeachers;
        this.totalSubjects = totalSubjects;
        this.totalClasses = totalClasses;
        this.totalAttendanceRecords = totalAttendanceRecords;
        this.totalResults = totalResults;
        this.lowAttendanceCount = lowAttendanceCount;
        this.lowAttendanceStudents = lowAttendanceStudents;
        this.recentActivities = recentActivities;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalTeachers() {
        return totalTeachers;
    }

    public void setTotalTeachers(long totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public long getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(long totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public long getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(long totalClasses) {
        this.totalClasses = totalClasses;
    }

    public long getTotalAttendanceRecords() {
        return totalAttendanceRecords;
    }

    public void setTotalAttendanceRecords(long totalAttendanceRecords) {
        this.totalAttendanceRecords = totalAttendanceRecords;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public long getLowAttendanceCount() {
        return lowAttendanceCount;
    }

    public void setLowAttendanceCount(long lowAttendanceCount) {
        this.lowAttendanceCount = lowAttendanceCount;
    }

    public List<AttendanceSummaryDto> getLowAttendanceStudents() {
        return lowAttendanceStudents;
    }

    public void setLowAttendanceStudents(List<AttendanceSummaryDto> lowAttendanceStudents) {
        this.lowAttendanceStudents = lowAttendanceStudents;
    }

    public List<String> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<String> recentActivities) {
        this.recentActivities = recentActivities;
    }
}
