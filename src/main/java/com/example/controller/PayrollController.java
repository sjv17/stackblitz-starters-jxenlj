package com.example.tasko.controller;

import com.example.tasko.model.Payroll;
import com.example.tasko.model.User;
import com.example.tasko.service.PayrollService;
import com.example.tasko.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/payroll")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private UserService userService;

    @GetMapping("/manage")
    public String managePendingPayrolls(Model model) {
        try {
            List<Payroll> pendingPayrolls = payrollService.getPendingPayrolls();
            List<User> users = userService.getAllUsers();
            model.addAttribute("payrolls", pendingPayrolls);
            model.addAttribute("users", users);
            return "payroll/manage";
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }
    }

    @GetMapping("/my-payroll")
    public String viewMyPayroll(Authentication authentication, Model model) {
        try {
            User user = userService.getUserByUsername(authentication.getName());
            List<Payroll> payrolls = payrollService.getUserPayroll(user);
            model.addAttribute("payrolls", payrolls);
            return "payroll/view";
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }
    }

    @PostMapping("/generate")
    public String generatePayroll(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            User user = userService.getUserById(userId);
            payrollService.generatePayroll(user, startDate, endDate);
            return "redirect:/payroll/manage";
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }
    }

    @PostMapping("/process/{id}")
    public String processPayment(@PathVariable Long id) {
        try {
            payrollService.processPayment(id);
            return "redirect:/payroll/manage";
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }
    }

    @GetMapping("/reports")
    public String payrollReports(Model model) {
        try {
            // Add logic for payroll reports
            return "payroll/reports";
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }
    }
}