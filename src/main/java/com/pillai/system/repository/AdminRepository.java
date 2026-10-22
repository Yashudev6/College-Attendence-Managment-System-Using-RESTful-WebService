package com.pillai.system.repository;

import com.pillai.system.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUserUsername(String username);
    Optional<Admin> findByUserId(Long userId);
}
