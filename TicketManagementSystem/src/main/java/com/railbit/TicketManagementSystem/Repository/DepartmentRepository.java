package com.railbit.TicketManagementSystem.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.railbit.TicketManagementSystem.Entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
	Optional<Department> findByName(String name);





}
