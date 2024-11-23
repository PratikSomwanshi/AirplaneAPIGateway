package com.wanda.repository;

import com.wanda.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {
    public Optional<Roles> findByAuthority(String authority);
}
