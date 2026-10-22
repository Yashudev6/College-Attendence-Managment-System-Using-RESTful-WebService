package com.pillai.system.repository;

import com.pillai.system.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findByDepartmentId(Long departmentId);
}
