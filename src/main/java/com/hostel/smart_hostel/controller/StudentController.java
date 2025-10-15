package com.hostel.smart_hostel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.hostel.smart_hostel.entity.Parent;
import com.hostel.smart_hostel.entity.Student;
import com.hostel.smart_hostel.repository.ParentRepository;
import com.hostel.smart_hostel.repository.StudentRepository;
import com.hostel.smart_hostel.service.EmailService;

import java.util.Random;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/leave")
    public String showLeaveForm(Model model) {
        model.addAttribute("student", new Student());
        return "leave_form"; // leave_form.html
    }

    @PostMapping("/leave")
    public String submitLeave(@ModelAttribute Student student, Model model) {
        // Find the parent associated with this student to get their email
        Parent parent = parentRepository.findByStudentId(student.getStudentId());
        if (parent != null) {
            student.setParentId(parent.getUsername());
        } else {
            model.addAttribute("error", "No parent is registered for the student ID: " + student.getStudentId() + ". Please contact administration.");
            return "leave_form";
        }
        // Generate OTP for parent
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        student.setOtpCode(otp);
        student.setOtpVerified(false);
        student.setParentStatus("Pending");
        student.setTeacherStatus("Pending");
        student.setHostelcoStatus("Pending");
        student.setHodStatus("Pending");
        student.setOverallStatus("Pending");

        // Send the OTP via email
        try {
            emailService.sendOtpEmail(parent.getEmail(), "OTP for Student Leave Request", otp);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }

        studentRepository.save(student);
        model.addAttribute("student", student);
        return "leave_success"; // leave_success.html
    }

    @GetMapping("/leave-details/{requestId}")
    public String showLeaveDetails(@PathVariable Long requestId, Model model) {
        Student student = studentRepository.findById(requestId).orElse(null);
        if (student != null && "Approved".equals(student.getOverallStatus())) {
            model.addAttribute("student", student);
            return "leave_details";
        }
        return "error_page"; // Or a page indicating the request is not found or not approved
    }

    @GetMapping("/qr/{requestId}")
    public ResponseEntity<byte[]> getQRCode(@PathVariable Long requestId) {
        Student student = studentRepository.findById(requestId).orElse(null);
        if (student != null && student.getQrCode() != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(student.getQrCode());
        }
        return ResponseEntity.notFound().build();
    }
}
