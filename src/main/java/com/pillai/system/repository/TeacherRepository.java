package com.pillai.system.repository;

import com.pillai.system.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByTeacherId(String teacherId);
    Optional<Teacher> findByUserUsername(String username);
    Optional<Teacher> findByUserId(Long userId);
    List<Teacher> findByDepartmentId(Long departmentId);
    boolean existsByTeacherId(String teacherId);
    boolean existsByEmail(String email);
}
