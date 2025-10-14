package com.hostel.smart_hostel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hostel.smart_hostel.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Parent: get leaves for their child by parentId and status
    List<Student> findByParentIdAndParentStatus(String parentId, String status);

    // Teacher: get leaves for their department by teacher status
    List<Student> findByDepartmentAndTeacherStatus(String department, String status);

    // Hostel Coordinator: get leaves for their department by hostel coordinator status
    List<Student> findByDepartmentAndHostelcoStatus(String department, String status);

    // HOD: get leaves for their department by HOD status
    List<Student> findByDepartmentAndHodStatus(String department, String status);

    // Parent: get a single student by studentId
    Student findByStudentId(String studentId); // <-- Added for parent login and approval
}
