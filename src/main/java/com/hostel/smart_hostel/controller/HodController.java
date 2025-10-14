package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.entity.Hod;
import com.hostel.smart_hostel.entity.Student;
import com.hostel.smart_hostel.repository.HodRepository;
import com.hostel.smart_hostel.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HodController {

    private final HodRepository hodRepo;
    private final StudentRepository studentRepo;

    public HodController(HodRepository hodRepo, StudentRepository studentRepo) {
        this.hodRepo = hodRepo;
        this.studentRepo = studentRepo;
    }

    @GetMapping("/hodLogin")
    public String loginPage() { return "hodLogin"; }

    @PostMapping("/hodLogin")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        Hod hod = hodRepo.findByUsername(username);
        if(hod != null && hod.getPassword().equals(password)) {
            List<Student> requests = studentRepo.findByDepartmentAndHodStatus(hod.getDepartment(), "Pending");
            model.addAttribute("hod", hod);
            model.addAttribute("requests", requests);
            return "hodDashboard";
        }
        model.addAttribute("error", "Invalid credentials");
        return "hodLogin";
    }

    @PostMapping("/approveByHod")
    public String approve(@RequestParam Long studentId) {
        Student student = studentRepo.findById(studentId).orElse(null);
        if(student != null) {
            student.setHodStatus("Approved");
            // If all approvals done → final
            if(student.isOtpVerified() &&
               student.getParentStatus().equals("Approved") &&
               student.getTeacherStatus().equals("Approved") &&
               student.getHostelcoStatus().equals("Approved")) {
                student.setOverallStatus("Approved");
                student.setQrCodeData("QR:" + student.getStudentId() + "|" + student.getFromDate() + "|" + student.getToDate());
            }
            studentRepo.save(student);
        }
        return "redirect:/hodLogin";
    }
}
