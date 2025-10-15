package com.hostel.smart_hostel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    private String studentId;        // unique student ID (e.g., S101)
    private String studentName;      // full name
    private Department department;       // e.g., Mechanical, ECE, etc.
    private String roomNumber;
    private String reason;
    private String fromDate;
    private String toDate;

    private String parentId;         // parent username or email (linked to Parent)
    private String otpCode;          // OTP sent to parent
    private boolean otpVerified = false;

    // Track status at each approval level
    private String status = "Pending Parent Approval";  // current stage
    private String parentStatus = "Pending";
    private String teacherStatus = "Pending";
    private String hostelcoStatus = "Pending";
    private String hodStatus = "Pending";
    private String overallStatus = "Pending";           // final overall approval

    private String qrCodeData;       // store QR code data/path (after HOD approval)
}
