// Suggested code may be subject to a license. Learn more: ~LicenseLog:3136003758.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.model.Task;
import com.example.model.Payroll;
import com.example.model.Attendance;
import com.example.service.TaskService;
import com.example.service.PayrollService;
import com.example.service.AttendanceService;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private AttendanceService attendanceService;

    // Task Management
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        logger.info("Task created: {}", createdTask);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        logger.info("Retrieved all tasks");
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task);
        logger.info("Task updated: {}", updatedTask);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        logger.info("Task deleted: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    // Payroll Management
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/payroll")
    public ResponseEntity<Payroll> createPayrollEntry(@RequestBody Payroll payroll) {
        Payroll createdPayroll = payrollService.createPayrollEntry(payroll);
        logger.info("Payroll entry created: {}", createdPayroll);
        return new ResponseEntity<>(createdPayroll, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/payroll")
    public ResponseEntity<List<Payroll>> getAllPayrollEntries() {
        List<Payroll> payrollEntries = payrollService.getAllPayrollEntries();
        logger.info("Retrieved all payroll entries");
        return new ResponseEntity<>(payrollEntries, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/payroll/{id}")
    public ResponseEntity<Payroll> updatePayrollEntry(@PathVariable Long id, @RequestBody Payroll payroll) {
        Payroll updatedPayroll = payrollService.updatePayrollEntry(id, payroll);
        logger.info("Payroll entry updated: {}", updatedPayroll);
        return new ResponseEntity<>(updatedPayroll, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/payroll/{id}")
    public ResponseEntity<Void> deletePayrollEntry(@PathVariable Long id) {
        payrollService.deletePayrollEntry(id);
        logger.info("Payroll entry deleted: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




    // Task Oversight
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tasks/oversight")
    public ResponseEntity<List<Task>> getTasksForOversight() {
        List<Task> tasks = taskService.getAllTasks(); // Or a specific oversight query
        logger.info("Retrieved tasks for oversight");
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }


    // Attendance Management
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/attendance")
    public ResponseEntity<Attendance> recordAttendance(@RequestBody Attendance attendance) {
        Attendance recordedAttendance = attendanceService.recordAttendance(attendance);
        logger.info("Attendance recorded: {}", recordedAttendance);
        return new ResponseEntity<>(recordedAttendance, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/attendance")
    public ResponseEntity<List<Attendance>> getAllAttendanceRecords() {
        List<Attendance> attendanceRecords = attendanceService.getAllAttendanceRecords();
        logger.info("Retrieved all attendance records");
        return new ResponseEntity<>(attendanceRecords, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/attendance/{id}")
    public ResponseEntity<Attendance> updateAttendanceRecord(@PathVariable Long id, @RequestBody Attendance attendance) {
        Attendance updatedAttendance = attendanceService.updateAttendanceRecord(id, attendance);
        logger.info("Attendance record updated: {}", updatedAttendance);
        return new ResponseEntity<>(updatedAttendance, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/attendance/{id}")
    public ResponseEntity<Void> deleteAttendanceRecord(@PathVariable Long id) {
        attendanceService.deleteAttendanceRecord(id);
        logger.info("Attendance record deleted: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    // Exception Handling (Example)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Error: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}