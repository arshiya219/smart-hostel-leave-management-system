
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
public class TeacherController {

    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;

    public TeacherController(TeacherRepository teacherRepo, StudentRepository studentRepo) {
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
    }

    @GetMapping("/teacherLogin")
    public String loginPage() { return "teacherLogin"; }

    @PostMapping("/teacherLogin")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        Teacher teacher = teacherRepo.findByUsername(username);
        if(teacher != null && teacher.getPassword().equals(password)) {
            List<Student> requests = studentRepo.findByDepartmentAndTeacherStatus(teacher.getDepartment(), "Pending");
            model.addAttribute("teacher", teacher);
            model.addAttribute("requests", requests);
            return "teacherDashboard";
        }
        model.addAttribute("error", "Invalid credentials");
        return "teacherLogin";
    }

    @PostMapping("/approveByTeacher")
    public String approve(@RequestParam Long studentId) {
        Student student = studentRepo.findById(studentId).orElse(null);
        if(student != null) {
            student.setTeacherStatus("Approved");
            studentRepo.save(student);
        }
        return "redirect:/teacherLogin";
    }
}
