package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.entity.Parent;
import com.hostel.smart_hostel.entity.Student;
import com.hostel.smart_hostel.repository.ParentRepository;
import com.hostel.smart_hostel.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;

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
        // Find all parent entries for the given username
        List<Parent> parentEntries = parentRepository.findAllByUsername(username);

        if (!parentEntries.isEmpty() && parentEntries.get(0).getPassword().equals(password)) {
            loggedInParent = parentEntries.get(0); // Store one instance for session

            // Fetch all students associated with this parent's username
            List<Student> students = new ArrayList<>();
            for (Parent p : parentEntries) {
                List<Student> studentRequests = studentRepository.findByStudentId(p.getStudentId());
                if (studentRequests != null && !studentRequests.isEmpty()) {
                    students.addAll(studentRequests);
                }
            }

            model.addAttribute("parent", parentEntries.get(0));
            model.addAttribute("students", students); 
            
            return "parent_dashboard"; // Redirect to a dashboard showing all children
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
 
        // Ensure the logged-in parent is associated with this student
        boolean isAuthorized = student != null && parentRepository.findByUsernameAndStudentId(loggedInParent.getUsername(), student.getStudentId()) != null;

        if (isAuthorized) {
            if (student.getOtpCode().equals(otpCode)) {
                student.setOtpVerified(true);
                student.setParentStatus("Approved");
                student.setStatus("Approved by Parent");
                studentRepository.save(student);
                model.addAttribute("success", "Parent approval successful for " + student.getStudentName() + "!");
            } else {
                model.addAttribute("error", "Invalid OTP!");
            }
        } else {
            model.addAttribute("error", "Student not found or you are not authorized!");
        }

        // Re-populate the dashboard with all student requests
        Parent parent = loggedInParent; 
        List<Student> students = new ArrayList<>();
        if (parent != null) {
            List<Parent> parentEntries = parentRepository.findAllByUsername(parent.getUsername());
            for (Parent p : parentEntries) {
                List<Student> studentRequests = studentRepository.findByStudentId(p.getStudentId());
                if (studentRequests != null) {
                    students.addAll(studentRequests);
                }
            }
        }
 
        model.addAttribute("parent", parent); 
        model.addAttribute("students", students);
        return "parent_dashboard";
    }

    @PostMapping("/reject")
    public String rejectRequest(@RequestParam Long requestId, Model model) {
        Student student = studentRepository.findById(requestId).orElse(null);

        // Ensure the logged-in parent is associated with this student
        boolean isAuthorized = student != null && loggedInParent != null && parentRepository.findByUsernameAndStudentId(loggedInParent.getUsername(), student.getStudentId()) != null;

        if (isAuthorized) {
            student.setParentStatus("Rejected");
            student.setOverallStatus("Rejected"); // Stop the workflow
            student.setStatus("Rejected by Parent");
            studentRepository.save(student);
            model.addAttribute("success", "Leave request for " + student.getStudentName() + " has been rejected.");
        } else {
            model.addAttribute("error", "Student not found or you are not authorized to reject this request.");
        }

        // Re-populate the dashboard with all student requests
        Parent parent = loggedInParent;
        List<Student> students = new ArrayList<>();
        if (parent != null) {
            List<Parent> parentEntries = parentRepository.findAllByUsername(parent.getUsername());
            for (Parent p : parentEntries) {
                List<Student> studentRequests = studentRepository.findByStudentId(p.getStudentId());
                if (studentRequests != null) {
                    students.addAll(studentRequests);
                }
            }
        }
        model.addAttribute("parent", parent);
        model.addAttribute("students", students);
        return "parent_dashboard";
    }

    // 4️⃣ Create Parent and link to student
    @GetMapping("/create")
    public String showCreateParentForm(Model model) {
        model.addAttribute("parent", new Parent());
        return "create_parent"; //create_parent.html
    }

    @PostMapping("/create")
    public String createParent(@ModelAttribute Parent parent, Model model) {
           try {
            // Check if this specific parent-student link already exists
            if (parentRepository.findByUsernameAndStudentId(parent.getUsername(), parent.getStudentId()) != null) {
                model.addAttribute("error", "This parent is already linked to this student.");
                model.addAttribute("parent", parent); // Keep user's input
                return "create_parent";
            }
 
            // Check if parent exists and update password if a new one is provided
            List<Parent> existingParentEntries = parentRepository.findAllByUsername(parent.getUsername());
            if (!existingParentEntries.isEmpty()) {
                String newPassword = parent.getPassword();
 
                // If a new password is provided, update it for all existing entries
                if (newPassword != null && !newPassword.isEmpty()) {
                    for (Parent p : existingParentEntries) {
                        String existingPassword = p.getPassword();
                        if(!newPassword.equals(existingPassword)) {
                        p.setPassword(newPassword);
                        }
                    }
                    parentRepository.saveAll(existingParentEntries);
                }
            }

            parentRepository.save(parent);
            model.addAttribute("message", "Parent created successfully!");
            model.addAttribute("parent", new Parent()); // Clear the form for the next entry
            return "create_parent";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during creation. Please try again.");
            model.addAttribute("parent", parent); // Keep user's input
            return "create_parent";
        }
    }





}
