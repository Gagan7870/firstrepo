package com.railbit.TicketManagementSystem.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.railbit.TicketManagementSystem.Entity.Priority;
import com.railbit.TicketManagementSystem.Entity.SLAConfiguration;

public interface SLAConfigurationRepository extends JpaRepository<SLAConfiguration, Long> {
    SLAConfiguration findByPriority(Priority priority);

}
