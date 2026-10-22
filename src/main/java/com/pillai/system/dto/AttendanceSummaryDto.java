package com.pillai.system.dto;

public class AttendanceSummaryDto {
    private Long studentId;
    private String studentNumber;
    private String studentName;
    private String className;
    private long totalClasses;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private double attendancePercentage;
    private boolean lowAttendance;

    public AttendanceSummaryDto() {
    }

    public AttendanceSummaryDto(Long studentId, String studentNumber, String studentName, String className, long totalClasses, long presentCount, long absentCount, long lateCount, double attendancePercentage, boolean lowAttendance) {
        this.studentId = studentId;
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.className = className;
        this.totalClasses = totalClasses;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.lateCount = lateCount;
        this.attendancePercentage = attendancePercentage;
        this.lowAttendance = lowAttendance;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(long totalClasses) {
        this.totalClasses = totalClasses;
    }

    public long getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(long presentCount) {
        this.presentCount = presentCount;
    }

    public long getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(long absentCount) {
        this.absentCount = absentCount;
    }

    public long getLateCount() {
        return lateCount;
    }

    public void setLateCount(long lateCount) {
        this.lateCount = lateCount;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public boolean isLowAttendance() {
        return lowAttendance;
    }

    public void setLowAttendance(boolean lowAttendance) {
        this.lowAttendance = lowAttendance;
    }
}
