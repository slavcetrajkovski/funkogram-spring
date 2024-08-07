package com.example.funkogram.app.user.repository;

import com.example.funkogram.app.user.domain.ERole;
import com.example.funkogram.app.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
