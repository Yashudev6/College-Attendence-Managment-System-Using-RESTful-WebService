package com.pillai.system.repository;

import com.pillai.system.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByStudentId(Long studentId);
    List<Result> findBySubjectId(Long subjectId);
    List<Result> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
    List<Result> findByStudentIdAndSemester(Long studentId, String semester);
}
