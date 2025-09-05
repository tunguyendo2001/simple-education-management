package com.example.studentapi.service.impl;

import com.example.studentapi.model.SemesterSchedule;
import com.example.studentapi.repository.SemesterScheduleRepository;
import com.example.studentapi.service.SemesterScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SemesterScheduleServiceImpl implements SemesterScheduleService {

    @Autowired
    private SemesterScheduleRepository scheduleRepository;

    @Override
    public SemesterSchedule createSchedule(SemesterSchedule schedule) {
        // Validate that there's no overlapping schedule
        boolean hasOverlap = scheduleRepository.hasOverlappingSchedule(
            schedule.getSemester(), 
            schedule.getYear(), 
            schedule.getClassName(),
            schedule.getStartDateTime(), 
            schedule.getEndDateTime()
        );
        
        if (hasOverlap) {
            throw new IllegalArgumentException("Overlapping schedule exists for the same class, semester, and year");
        }
        
        return scheduleRepository.save(schedule);
    }

    @Override
    public SemesterSchedule updateSchedule(Long id, SemesterSchedule schedule) {
        SemesterSchedule existingSchedule = findById(id);
        if (existingSchedule == null) {
            throw new IllegalArgumentException("Schedule not found with id: " + id);
        }
        
        // Check for overlaps excluding current schedule
        if (existingSchedule.getSemester() != schedule.getSemester() ||
            existingSchedule.getYear() != schedule.getYear() ||
            !existingSchedule.getClassName().equals(schedule.getClassName()) ||
            !existingSchedule.getStartDateTime().equals(schedule.getStartDateTime()) ||
            !existingSchedule.getEndDateTime().equals(schedule.getEndDateTime())) {
            
            boolean hasOverlap = scheduleRepository.hasOverlappingSchedule(
                schedule.getSemester(), 
                schedule.getYear(), 
                schedule.getClassName(),
                schedule.getStartDateTime(), 
                schedule.getEndDateTime()
            );
            
            if (hasOverlap) {
                throw new IllegalArgumentException("Overlapping schedule exists for the same class, semester, and year");
            }
        }
        
        schedule.setId(id);
        return scheduleRepository.save(schedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Schedule not found with id: " + id);
        }
        scheduleRepository.deleteById(id);
    }

    @Override
    public SemesterSchedule findById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    @Override
    public List<SemesterSchedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public List<SemesterSchedule> findActiveSchedules() {
        return scheduleRepository.findByIsActiveTrue();
    }

    @Override
    public boolean isScoreEntryAllowed(int semester, int year, String className) {
        Optional<SemesterSchedule> schedule = scheduleRepository.findActiveSchedule(semester, year, className);
        return schedule.isPresent() && schedule.get().isCurrentlyAllowed();
    }

    @Override
    public SemesterSchedule findActiveScheduleForClass(int semester, int year, String className) {
        return scheduleRepository.findActiveSchedule(semester, year, className).orElse(null);
    }

    @Override
    @Scheduled(fixedRate = 60000) // Check every minute
    public void lockExpiredSchedules() {
        LocalDateTime now = LocalDateTime.now();
        List<SemesterSchedule> expiredSchedules = scheduleRepository.findSchedulesToLock(now);
        
        for (SemesterSchedule schedule : expiredSchedules) {
            schedule.setLocked(true);
            scheduleRepository.save(schedule);
            System.out.println("Automatically locked schedule: " + schedule.getScheduleName() + 
                             " for class " + schedule.getClassName());
        }
    }

    @Override
    public List<SemesterSchedule> findByYearAndSemester(int year, int semester) {
        return scheduleRepository.findByYearAndSemesterOrderByClassNameAsc(year, semester);
    }
}