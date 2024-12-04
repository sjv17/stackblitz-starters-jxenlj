package com.example.tasko.service;

import com.example.tasko.model.Attendance;
import com.example.tasko.model.Enterprise;
import com.example.tasko.model.User;
import com.example.tasko.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Transactional
    public Attendance logAttendance(Attendance attendance) {
        // Check if attendance already exists for the day
        Attendance existingAttendance = attendanceRepository.findByUserAndDate(
            attendance.getUser(), attendance.getDate());
        
        if (existingAttendance != null) {
            existingAttendance.setStatus(attendance.getStatus());
            return attendanceRepository.save(existingAttendance);
        }
        
        return attendanceRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getUserAttendance(User user, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByUserAndDateBetween(user, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getMonthlyAttendance(LocalDate month) {
        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());
        return attendanceRepository.findByDateBetween(startDate, endDate);
    }

    public long countTodayAttendanceByEnterprise(Enterprise enterprise) {
        return attendanceRepository.countByUserEnterpriseAndDate(enterprise, LocalDate.now());
    }

    public List<Attendance> getRecentAttendanceByEnterprise(Enterprise enterprise) {
        return attendanceRepository.findTop5ByUserEnterpriseOrderByDateDesc(enterprise);
    }

    public Attendance getTodayAttendance(User user) {
        return attendanceRepository.findByUserAndDate(user, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Attendance> getTodayAttendanceRecords(Enterprise enterprise) {
        return attendanceRepository.findByUserEnterpriseAndDate(enterprise, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAttendanceCalendarData(Enterprise enterprise) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        
        List<Attendance> attendanceList = attendanceRepository.findByUserAndDateBetween(
            null, startOfMonth, endOfMonth);

        Map<String, Object> calendarData = new HashMap<>();
        calendarData.put("attendance", attendanceList);
        calendarData.put("startDate", startOfMonth);
        calendarData.put("endDate", endOfMonth);
        
        return calendarData;
    }
}