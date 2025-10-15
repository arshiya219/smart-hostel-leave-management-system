package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    Parent findByUsername(String username);
    List<Parent> findAllByUsername(String username);
    Parent findByUsernameAndStudentId(String username, String studentId);
}
