package com.intuit.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.intuit.model.entity.Role;
import com.intuit.model.entity.URole;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(URole name);

}
