package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.entity.Parent;
import com.hostel.smart_hostel.entity.Student;
import com.hostel.smart_hostel.repository.ParentRepository;
import com.hostel.smart_hostel.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/parent")
public class ParentController {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Parent loggedInParent;

    // 1️⃣ Show Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "parent_login";
    }

    // 2️⃣ Handle Login
    @PostMapping("/login")
    public String loginParent(@RequestParam String username,
                              @RequestParam String password,
                              Model model) {
        Parent parent = parentRepository.findByUsername(username);
        if (parent != null && parent.getPassword().equals(password)) {
            loggedInParent = parent;

            Student student = studentRepository.findByStudentId(parent.getStudentId());
            model.addAttribute("parent", parent);
            model.addAttribute("student", student);

            return "parent_dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "parent_login";
        }
    }

    // 3️⃣ Approve Student Request (after OTP)
    @PostMapping("/approve")
    public String approveRequest(@RequestParam Long requestId,
                                 @RequestParam String otpCode,
                                 Model model) {
        Student student = studentRepository.findById(requestId).orElse(null);

        if (student != null && student.getOtpCode().equals(otpCode)) {
            student.setOtpVerified(true);
            student.setParentStatus("Approved");
            student.setStatus("Approved by Parent");
            studentRepository.save(student);

            model.addAttribute("success", "Parent approval successful!");
        } else {
            model.addAttribute("error", "Invalid OTP or student not found!");
        }

        Parent parent = loggedInParent;
        model.addAttribute("parent", parent);
        model.addAttribute("student", student);

        return "parent_dashboard";
    }
}
