package com.hostel.smart_hostel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hostel.smart_hostel.entity.Department;
import com.hostel.smart_hostel.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Parent: get leaves for their child by parentId and status
    List<Student> findByParentIdAndParentStatus(String parentId, String status);

    // Teacher: get leaves for their department by teacher status
    List<Student> findByDepartmentAndTeacherStatusAndParentStatus(Department department, String teacherStatus, String parentStatus);

    // Hostel Coordinator: get leaves for their department by hostel coordinator status
    List<Student> findByDepartmentAndHostelcoStatusAndTeacherStatus(Department department, String hostelcoStatus, String teacherStatus);

    // HOD: get leaves for their department by HOD status
    List<Student> findByDepartmentAndHodStatusAndHostelcoStatus(Department department, String hodStatus, String hostelcoStatus);

    // Parent: get a single student by studentId
    List<Student> findByStudentId(String studentId); // A student can have multiple leave requests
}
