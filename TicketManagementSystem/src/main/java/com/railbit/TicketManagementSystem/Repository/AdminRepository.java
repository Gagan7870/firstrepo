package com.railbit.TicketManagementSystem.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.railbit.TicketManagementSystem.Entity.Admin;

public interface AdminRepository extends JpaRepository<Admin,Long> {

	   Optional<Admin> findByEmail(String email);
	   Optional<Admin> findByResetToken(String resetToken);


}


