package com.example.tasko.service;

import com.example.tasko.model.Enterprise;
import com.example.tasko.model.Task;
import com.example.tasko.model.User;
import com.example.tasko.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tasko.exception.TaskNotFoundException;
import com.example.tasko.exception.InvalidTaskException;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByAssignedUsersContaining(user);
    }

    public Page<Task> getTasksPaginated(Pageable pageable, String sort, String filter) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sort.startsWith("-")) {
            direction = Sort.Direction.DESC;
            sort = sort.substring(1);
        }
        
        Sort sortObj = Sort.by(direction, sort);
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);
        
        if (filter != null && !filter.isEmpty()) {
            return taskRepository.findByTitleContainingIgnoreCase(filter, pageableWithSort);
        }
        
        return taskRepository.findAll(pageableWithSort);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Transactional
    public Task createTask(Task task) {
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            throw new InvalidTaskException("Task title cannot be empty");
        }
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Task task) {
        if (task.getId() == null) {
            throw new InvalidTaskException("Task ID cannot be null for update");
        }
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            throw new InvalidTaskException("Task title cannot be empty");
        }
        Optional<Task> existingTask = taskRepository.findById(task.getId());       
        if (existingTask.isPresent()) {            
            Task updatedTask = existingTask.get();
            updatedTask.setTitle(task.getTitle());
            updatedTask.setDescription(task.getDescription());
            updatedTask.setDueDate(task.getDueDate());
            updatedTask.setCompleted(task.isCompleted());
            if (task.getAssignedUsers() != null) {
                updatedTask.setAssignedUsers(task.getAssignedUsers());
            }
            return taskRepository.save(updatedTask);
        }
        throw new TaskNotFoundException("Task not found with id: " + task.getId());
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id); 
    }

    @Transactional
    public Task completeTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        task.setCompleted(true);
        return taskRepository.save(task);
    }

    public long countActiveTasksByEnterprise(Enterprise enterprise) {
        return taskRepository.countByEnterpriseAndCompletedFalse(enterprise);
    }

    public List<Task> getRecentTasksByEnterprise(Enterprise enterprise) {
        if (enterprise == null) {
            throw new InvalidTaskException("Enterprise cannot be null");
        }
        return taskRepository.findTop5ByEnterpriseAndCompletedFalseOrderByDueDateDesc(enterprise);
    }

    public List<Task> getRecentTasksByUser(User user) {
        if (user == null) {
            throw new InvalidTaskException("User cannot be null");
        }
        return taskRepository.findTop5ByAssignedUsersContainingAndCompletedFalseOrderByDueDateAsc(user);
    }
}