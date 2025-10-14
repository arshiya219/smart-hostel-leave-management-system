
package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.entity.Hod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HodRepository extends JpaRepository<Hod, Long> {
    Hod findByUsername(String username);
}
