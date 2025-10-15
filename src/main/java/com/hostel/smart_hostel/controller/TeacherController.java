
package com.hostel.smart_hostel.controller;

import com.hostel.smart_hostel.entity.Student;
import com.hostel.smart_hostel.entity.Teacher;
import com.hostel.smart_hostel.repository.StudentRepository;
import com.hostel.smart_hostel.repository.TeacherRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;

    private Teacher loggedInTeacher; // Basic session management

    public TeacherController(TeacherRepository teacherRepo, StudentRepository studentRepo) {
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
    }

    @GetMapping("/create")
    public String showCreateTeacherForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        return "create_teacher";
    }

    @PostMapping("/create")
    public String createTeacher(@ModelAttribute Teacher teacher, Model model) {
        try {
            if (teacherRepo.findByUsername(teacher.getUsername()) != null) {
                model.addAttribute("error", "Username already exists. Please choose a different one.");
                model.addAttribute("teacher", teacher);
                return "create_teacher";
            }
            teacherRepo.save(teacher);
            model.addAttribute("message", "Teacher created successfully!");
            model.addAttribute("teacher", new Teacher()); // Clear the form
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during creation: " + e.getMessage());
            model.addAttribute("teacher", teacher);
        }
        return "create_teacher";
    }

    @GetMapping("/login")
    public String loginPage() { return "teacherLogin"; }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        Teacher teacher = teacherRepo.findByUsername(username);
        if (teacher != null && teacher.getPassword().equals(password)) {
            loggedInTeacher = teacher;
            List<Student> requests = studentRepo.findByDepartmentAndTeacherStatusAndParentStatus(teacher.getDepartment(), "Pending", "Approved");
            model.addAttribute("teacher", teacher);
            model.addAttribute("requests", requests);
            return "teacherDashboard";
        }
        model.addAttribute("error", "Invalid credentials");
        return "teacherLogin";
    }

    @PostMapping("/approve")
    public String approve(@RequestParam Long requestId, Model model) {
        Student student = studentRepo.findById(requestId).orElse(null);
        if (student != null && loggedInTeacher != null && student.getDepartment() == loggedInTeacher.getDepartment()) {
            student.setTeacherStatus("Approved");
            studentRepo.save(student);
            model.addAttribute("message", "Request for " + student.getStudentName() + " approved successfully.");
        } else {
            model.addAttribute("error", "Failed to approve request. It may no longer exist or you are not authorized.");
        }
        // Refresh dashboard
        List<Student> requests = studentRepo.findByDepartmentAndTeacherStatusAndParentStatus(loggedInTeacher.getDepartment(), "Pending", "Approved");
        model.addAttribute("teacher", loggedInTeacher);
        model.addAttribute("requests", requests);
        return "teacherDashboard";
    }

    @PostMapping("/reject")
    public String reject(@RequestParam Long requestId, Model model) {
        Student student = studentRepo.findById(requestId).orElse(null);
        if (student != null && loggedInTeacher != null && student.getDepartment() == loggedInTeacher.getDepartment()) {
            student.setTeacherStatus("Rejected");
            student.setOverallStatus("Rejected"); // Stop the workflow
            student.setStatus("Rejected by Teacher");
            studentRepo.save(student);
            model.addAttribute("message", "Request for " + student.getStudentName() + " has been rejected.");
        } else {
            model.addAttribute("error", "Failed to reject request. It may no longer exist or you are not authorized.");
        }
        // Refresh dashboard
        List<Student> requests = studentRepo.findByDepartmentAndTeacherStatusAndParentStatus(loggedInTeacher.getDepartment(), "Pending", "Approved");
        model.addAttribute("teacher", loggedInTeacher);
        model.addAttribute("requests", requests);
        return "teacherDashboard";
    }
}
