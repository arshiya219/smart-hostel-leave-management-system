package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    Parent findByUsername(String username);
}
