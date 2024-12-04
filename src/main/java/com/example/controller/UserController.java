// Suggested code may be subject to a license. Learn more: ~LicenseLog:3001665355.
// Suggested code may be subject to a license. Learn more: ~LicenseLog:3973260216.
package com.example.tasko.controller;

import com.example.tasko.model.Task;
import com.example.tasko.model.User;
import com.example.tasko.model.UserRole;
import com.example.tasko.service.EnterpriseService;
import com.example.tasko.service.TaskService;
import com.example.tasko.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private EnterpriseService enterpriseService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
        model.addAttribute("roles", UserRole.values());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
            model.addAttribute("roles", UserRole.values());
            return "register";
        }
        try {
            User createdUser = userService.createUser(user);
            logger.info("New user registered: {} (ID: {})", createdUser.getUsername(), createdUser.getId());
        } catch (Exception e) {
            bindingResult.rejectValue("username", "error.user", "An account already exists for this username.");
            model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
            model.addAttribute("roles", UserRole.values());
            logger.error("Error registering user: {} - {}", user.getUsername(), e.getMessage(), e);
            return "register";
        }
        return "redirect:/login?registered";
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/tasks")
    public String showUserTasks(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user.getRole() == UserRole.ADMIN) {
            model.addAttribute("tasks", taskService.getAllTasks());
        } else {
            model.addAttribute("tasks", taskService.getTasksAssignedToUser(user.getId()));
        }
        return "tasks";
    }

    @PostMapping("/tasks/{id}/complete")
    public String markTaskAsComplete(@PathVariable("id") Long taskId, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);
            Task task = taskService.getTaskById(taskId);

            if (task == null) {
                redirectAttributes.addFlashAttribute("error", "Task not found.");
                return "redirect:/tasks";
            }

            if (user.getRole() != UserRole.ADMIN && !task.getAssignee().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access to task.");
                return "redirect:/tasks";
            }

            taskService.markTaskAsComplete(taskId);
            redirectAttributes.addFlashAttribute("success", "Task marked as complete.");
            logger.info("User {} marked task {} as complete.", username, taskId);

        } catch (Exception e) {
            logger.error("Error marking task as complete: {} - {}", taskId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error marking task as complete.");
        }

        return "redirect:/tasks"; 
    }

    @GetMapping("/users")
    public String listUsers(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user.getRole() == UserRole.ADMIN) {
            model.addAttribute("users", userService.getAllUsers());
            return "users"; 
        } else {
            return "redirect:/tasks"; 
        }
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable("id") Long userId, Model model, Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);
        if (currentUser.getRole() == UserRole.ADMIN) {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
            model.addAttribute("roles", UserRole.values());
            return "edit-user";
        } else {
            return "redirect:/tasks"; 
        }
    }

    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable("id") Long userId, @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "edit-user";
        }
        userService.updateUser(user);
        return "redirect:/users";
    }
}