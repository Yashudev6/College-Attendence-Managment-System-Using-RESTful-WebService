package com.pillai.system.repository;

import com.pillai.system.entity.Attendance;
import com.pillai.system.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findBySubjectId(Long subjectId);
    List<Attendance> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
    List<Attendance> findByStudentIdAndDate(Long studentId, LocalDate date);
    boolean existsByStudentIdAndSubjectIdAndDate(Long studentId, Long subjectId, LocalDate date);
    
    long countByStudentId(Long studentId);
    long countByStudentIdAndStatus(Long studentId, AttendanceStatus status);
}
