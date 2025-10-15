package com.hostel.smart_hostel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.hostel.smart_hostel.entity.Student;
import com.hostel.smart_hostel.repository.StudentRepository;

import java.util.Random;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/leave")
    public String showLeaveForm(Model model) {
        model.addAttribute("student", new Student());
        return "leave_form"; // leave_form.html
    }

    @PostMapping("/leave")
    public String submitLeave(@ModelAttribute Student student, Model model) {
        // Generate OTP for parent
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        student.setOtpCode(otp);
        student.setOtpVerified(false);
        student.setParentStatus("Pending");
        student.setTeacherStatus("Pending");
        student.setHostelcoStatus("Pending");
        student.setHodStatus("Pending");
        student.setOverallStatus("Pending");

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
