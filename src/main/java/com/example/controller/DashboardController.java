package com.example.tasko.controller;

import com.example.tasko.model.*;
import com.example.tasko.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private PayrollService payrollService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            logger.info("Accessing dashboard for user: {}", authentication.getName());
            User user = userService.getUserByUsername(authentication.getName());
            Enterprise enterprise = user.getEnterprise();
            
            if (enterprise == null) {
                logger.error("No enterprise found for user: {}", user.getUsername());
                return "error/500";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("enterprise", enterprise);

            if (user.getRole() == UserRole.ADMIN) {
                logger.info("Setting up admin dashboard for user: {}", user.getUsername());
                return setupAdminDashboard(model, enterprise);
            } else {
                logger.info("Setting up user dashboard for user: {}", user.getUsername());
                return setupUserDashboard(model, user);
            }
        } catch (Exception e) {
            logger.error("Error accessing dashboard", e);
            return "error/500";
        }
    }

    private String setupAdminDashboard(Model model, Enterprise enterprise) {
        try {
            // Statistics
            model.addAttribute("userCount", userService.countUsersByEnterprise(enterprise));
            model.addAttribute("activeTaskCount", taskService.countActiveTasksByEnterprise(enterprise));
            model.addAttribute("todayAttendanceCount", attendanceService.countTodayAttendanceByEnterprise(enterprise));

            // Tasks
            model.addAttribute("tasks", taskService.getAllTasks());
            model.addAttribute("recentTasks", taskService.getRecentTasksByEnterprise(enterprise));
            model.addAttribute("users", userService.getAllUsers());

            // Attendance
            model.addAttribute("recentAttendance", attendanceService.getRecentAttendanceByEnterprise(enterprise));
            model.addAttribute("todayAttendance", attendanceService.getTodayAttendanceRecords(enterprise));

            // Payroll
            model.addAttribute("pendingPayrolls", payrollService.getPendingPayrolls());

            return "dashboard/admin";
        } catch (Exception e) {
            logger.error("Error setting up admin dashboard", e);
            return "error/500";
        }
    }

    private String setupUserDashboard(Model model, User user) {
        try {
            // Tasks
            List<Task> userTasks = taskService.getTasksByUser(user);
            model.addAttribute("tasks", userTasks);
            model.addAttribute("recentTasks", taskService.getRecentTasksByUser(user));
            
            // Task Statistics
            long completedTasks = userTasks.stream().filter(Task::isCompleted).count();
            model.addAttribute("completedTasksCount", completedTasks);
            model.addAttribute("pendingTasksCount", userTasks.size() - completedTasks);

            // Attendance
            Attendance todayAttendance = attendanceService.getTodayAttendance(user);
            model.addAttribute("todayAttendance", todayAttendance);
            
            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            List<Attendance> monthlyAttendance = attendanceService.getUserAttendance(
                user, 
                startOfMonth,
                LocalDate.now()
            );
            model.addAttribute("monthlyAttendance", monthlyAttendance);

            // Attendance Statistics
            long presentDays = monthlyAttendance.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();
            long lateDays = monthlyAttendance.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.LATE)
                .count();
            long halfDays = monthlyAttendance.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.HALF_DAY)
                .count();
            long absentDays = monthlyAttendance.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();

            model.addAttribute("presentDays", presentDays);
            model.addAttribute("lateDays", lateDays);
            model.addAttribute("halfDays", halfDays);
            model.addAttribute("absentDays", absentDays);
            model.addAttribute("attendanceRate", calculateAttendanceRate(presentDays, monthlyAttendance.size()));

            // Payroll
            model.addAttribute("payrollHistory", payrollService.getUserPayroll(user));

            return "dashboard/user";
        } catch (Exception e) {
            logger.error("Error setting up user dashboard", e);
            return "error/500";
        }
    }

    private double calculateAttendanceRate(long presentDays, long totalDays) {
        if (totalDays == 0) return 0;
        return (double) presentDays / totalDays * 100;
    }

    @GetMapping("/api/attendance/calendar")
    @ResponseBody
    public Map<String, Object> getAttendanceCalendarData(Authentication authentication) {
        try {
            User user = userService.getUserByUsername(authentication.getName());
            return attendanceService.getAttendanceCalendarData(user.getEnterprise());
        } catch (Exception e) {
            logger.error("Error loading attendance calendar data", e);
            throw new RuntimeException("Failed to load attendance calendar data");
        }
    }
}