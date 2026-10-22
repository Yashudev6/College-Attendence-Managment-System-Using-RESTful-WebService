package com.pillai.system.repository;

import com.pillai.system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByUserUsername(String username);
    Optional<Student> findByUserId(Long userId);
    List<Student> findBySchoolClassId(Long classId);
    List<Student> findByDepartmentId(Long departmentId);
    boolean existsByStudentId(String studentId);
    boolean existsByEmail(String email);

    @Query("SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.studentId) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchStudents(@Param("query") String query);
}
