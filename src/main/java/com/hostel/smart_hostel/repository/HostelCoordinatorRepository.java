package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.entity.HostelCoordinator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostelCoordinatorRepository extends JpaRepository<HostelCoordinator, Long> {
    HostelCoordinator findByUsername(String username);
}