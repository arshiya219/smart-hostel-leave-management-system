package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.entity.Hod;
import com.hostel.smart_hostel.entity.Student;
import com.hostel.smart_hostel.utility.QRCodeGenerator;
import com.hostel.smart_hostel.repository.HodRepository;
import com.hostel.smart_hostel.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hod")
public class HodController {

    private final HodRepository hodRepo;
    private final StudentRepository studentRepo;
    private Hod loggedInHod;

    public HodController(HodRepository hodRepo, StudentRepository studentRepo) {
        this.hodRepo = hodRepo;
        this.studentRepo = studentRepo;
    }

    @GetMapping("/create")
    public String showCreateHodForm(Model model) {
        model.addAttribute("hod", new Hod());
        return "create_hod";
    }

    @PostMapping("/create")
    public String createHod(@ModelAttribute Hod hod, Model model) {
        try {
            if (hodRepo.findByUsername(hod.getUsername()) != null) {
                model.addAttribute("error", "Username already exists.");
                model.addAttribute("hod", hod);
                return "create_hod";
            }
            hodRepo.save(hod);
            model.addAttribute("message", "HOD created successfully!");
            model.addAttribute("hod", new Hod());
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during creation: " + e.getMessage());
            model.addAttribute("hod", hod);
        }
        return "create_hod";
    }

    @GetMapping("/login")
    public String loginPage() { return "hodLogin"; }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        Hod hod = hodRepo.findByUsername(username);
        if(hod != null && hod.getPassword().equals(password)) {
            loggedInHod = hod;
            List<Student> requests = studentRepo.findByDepartmentAndHodStatusAndHostelcoStatus(hod.getDepartment(), "Pending", "Approved");
            model.addAttribute("hod", hod);
            model.addAttribute("requests", requests);
            return "hodDashboard";
        }
        model.addAttribute("error", "Invalid credentials");
        return "hodLogin";
    }

    @PostMapping("/approve")
    public String approve(@RequestParam Long requestId, Model model) {
        Student student = studentRepo.findById(requestId).orElse(null);
        if(student != null && loggedInHod != null && student.getDepartment() == loggedInHod.getDepartment()) {
            student.setHodStatus("Approved");
            student.setOverallStatus("Approved");
            student.setStatus("Completely Approved");
            // Generate QR Code
            try {
                // Embed leave details directly into the QR code as text
                String qrCodeText ="--- Leave Details ---\n" +
                                  "Student ID: " + student.getStudentId() + "\n" +
                                  "Name: " + student.getStudentName() + "\n" +
                                  "Department: " + student.getDepartment() + "\n" +
                                  "College: SRM\n" +
                                  "From: " + student.getFromDate() + "\n" +
                                  "To: " + student.getToDate() + "\n" +
                                  "Reason: " + student.getReason() + "\n" +
                                  "Status: " + student.getStatus();

                student.setQrCode(QRCodeGenerator.getQRCodeImage(qrCodeText, 250, 250));
                studentRepo.save(student);
                model.addAttribute("student", student);
                return "hod_approval_success"; // Forward to the new success page
            } catch (Exception e) {
                model.addAttribute("error", "Could not generate QR Code for the request.");
            }
        } else {
            model.addAttribute("error", "Failed to approve request.");
        }
        // On error, refresh the dashboard with an error message
        List<Student> requests = studentRepo.findByDepartmentAndHodStatusAndHostelcoStatus(loggedInHod.getDepartment(), "Pending", "Approved");
        model.addAttribute("hod", loggedInHod);
        model.addAttribute("requests", requests);
        return "hodDashboard";
    }

    @PostMapping("/reject")
    public String reject(@RequestParam Long requestId, Model model) {
        Student student = studentRepo.findById(requestId).orElse(null);
        if (student != null && loggedInHod != null && student.getDepartment() == loggedInHod.getDepartment()) {
            student.setHodStatus("Rejected");
            student.setOverallStatus("Rejected");
            student.setStatus("Rejected by HOD");
            studentRepo.save(student);
            model.addAttribute("message", "Request for " + student.getStudentName() + " has been rejected.");
        } else {
            model.addAttribute("error", "Failed to reject request.");
        }
        // Refresh dashboard
        List<Student> requests = studentRepo.findByDepartmentAndHodStatusAndHostelcoStatus(loggedInHod.getDepartment(), "Pending", "Approved");
        model.addAttribute("hod", loggedInHod);
        model.addAttribute("requests", requests);
        return "hodDashboard";
    }
}
