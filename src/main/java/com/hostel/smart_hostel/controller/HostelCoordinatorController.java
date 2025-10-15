package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.entity.HostelCoordinator;
import com.hostel.smart_hostel.entity.Student;
import com.hostel.smart_hostel.repository.HostelCoordinatorRepository;
import com.hostel.smart_hostel.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hostel-coordinator")
public class HostelCoordinatorController {

    private final HostelCoordinatorRepository coordinatorRepo;
    private final StudentRepository studentRepo;
    private HostelCoordinator loggedInCoordinator;

    public HostelCoordinatorController(HostelCoordinatorRepository coordinatorRepo, StudentRepository studentRepo) {
        this.coordinatorRepo = coordinatorRepo;
        this.studentRepo = studentRepo;
    }

    @GetMapping("/create")
    public String showCreateCoordinatorForm(Model model) {
        model.addAttribute("coordinator", new HostelCoordinator());
        return "create_hostelCoordinator";
    }

    @PostMapping("/create")
    public String createCoordinator(@ModelAttribute HostelCoordinator coordinator, Model model) {
        try {
            if (coordinatorRepo.findByUsername(coordinator.getUsername()) != null) {
                model.addAttribute("error", "Username already exists.");
                model.addAttribute("coordinator", coordinator);
                return "create_hostelCoordinator";
            }
            coordinatorRepo.save(coordinator);
            model.addAttribute("message", "Hostel Coordinator created successfully!");
            model.addAttribute("coordinator", new HostelCoordinator());
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during creation: " + e.getMessage());
            model.addAttribute("coordinator", coordinator);
        }
        return "create_hostelCoordinator";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "hostelCoordinatorLogin";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        HostelCoordinator coordinator = coordinatorRepo.findByUsername(username);
        if (coordinator != null && coordinator.getPassword().equals(password)) {
            loggedInCoordinator = coordinator;
            List<Student> requests = studentRepo.findByDepartmentAndHostelcoStatusAndTeacherStatus(coordinator.getDepartment(), "Pending", "Approved");
            model.addAttribute("coordinator", coordinator);
            model.addAttribute("requests", requests);
            return "hostelCoordinatorDashboard";
        }
        model.addAttribute("error", "Invalid credentials");
        return "hostelCoordinatorLogin";
    }

    @PostMapping("/approve")
    public String approve(@RequestParam Long requestId, Model model) {
        Student student = studentRepo.findById(requestId).orElse(null);
        if (student != null && loggedInCoordinator != null && student.getDepartment() == loggedInCoordinator.getDepartment()) {
            student.setHostelcoStatus("Approved");
            student.setStatus("Approved by Hostel Coordinator");
            studentRepo.save(student);
            model.addAttribute("message", "Request for " + student.getStudentName() + " approved successfully.");
        } else {
            model.addAttribute("error", "Failed to approve request.");
        }
        // Refresh dashboard
        List<Student> requests = studentRepo.findByDepartmentAndHostelcoStatusAndTeacherStatus(loggedInCoordinator.getDepartment(), "Pending", "Approved");
        model.addAttribute("coordinator", loggedInCoordinator);
        model.addAttribute("requests", requests);
        return "hostelCoordinatorDashboard";
    }

    @PostMapping("/reject")
    public String reject(@RequestParam Long requestId, Model model) {
        Student student = studentRepo.findById(requestId).orElse(null);
        if (student != null && loggedInCoordinator != null && student.getDepartment() == loggedInCoordinator.getDepartment()) {
            student.setHostelcoStatus("Rejected");
            student.setOverallStatus("Rejected");
            student.setStatus("Rejected by Hostel Coordinator");
            studentRepo.save(student);
            model.addAttribute("message", "Request for " + student.getStudentName() + " has been rejected.");
        } else {
            model.addAttribute("error", "Failed to reject request.");
        }
        // Refresh dashboard
        List<Student> requests = studentRepo.findByDepartmentAndHostelcoStatusAndTeacherStatus(loggedInCoordinator.getDepartment(), "Pending", "Approved");
        model.addAttribute("coordinator", loggedInCoordinator);
        model.addAttribute("requests", requests);
        return "hostelCoordinatorDashboard";
    }
}