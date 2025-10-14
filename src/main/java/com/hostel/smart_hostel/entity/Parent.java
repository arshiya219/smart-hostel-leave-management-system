package com.hostel.smart_hostel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String parentName;
    private String username;
    private String password;

    private String studentId;      // Linked to Student.studentId
    private String department;     // Department of the student
}
