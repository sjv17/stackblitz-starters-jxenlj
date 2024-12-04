package com.example.tasko.controller;

import com.example.tasko.model.Task;
import com.example.tasko.model.User;
import com.example.tasko.service.TaskService;
import com.example.tasko.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sort,
            @RequestParam(required = false) String filter,
            Model model) {
        try {
            Page<Task> taskPage = taskService.getTasksPaginated(PageRequest.of(page, size), sort, filter);
            model.addAttribute("tasks", taskPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", taskPage.getTotalPages());
            model.addAttribute("sort", sort);
            model.addAttribute("filter", filter);
            return "tasks/list";
        } catch (Exception e) {
            return "redirect:/error/500";
        }
    }

    @PostMapping("/create")
    public String createTask(@Valid @ModelAttribute Task task, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("users", userService.getAllUsers());
                return "tasks/create";
            }
            taskService.createTask(task);
            redirectAttributes.addFlashAttribute("success", "Task created successfully");
            return "redirect:/tasks";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create task");
            return "redirect:/tasks";
        }
    }

    @PostMapping("/edit/{id}")
    public String editTask(@PathVariable Long id, @Valid @ModelAttribute Task task, 
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("users", userService.getAllUsers());
                return "tasks/edit";
            }
            task.setId(id);
            taskService.updateTask(task);
            redirectAttributes.addFlashAttribute("success", "Task updated successfully");
            return "redirect:/tasks";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update task");
            return "redirect:/tasks";
        }
    }

    @PostMapping("/{id}/complete")
    @ResponseBody
    public ResponseEntity<String> completeTask(@PathVariable Long id) {
        try {
            Task task = taskService.completeTask(id);
            if (task != null) {
                return ResponseEntity.ok("Task completed successfully");
            }
            return ResponseEntity.badRequest().body("Failed to complete task");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred");
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            taskService.deleteTask(id);
            redirectAttributes.addFlashAttribute("success", "Task deleted successfully");
            return "redirect:/tasks";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete task");
            return "redirect:/tasks";
        }
    }
}