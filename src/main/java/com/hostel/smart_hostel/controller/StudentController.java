package com.hostel.smart_hostel.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
}
