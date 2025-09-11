package com.example.studentapi.service;

import com.example.studentapi.model.SemesterSchedule;
import java.util.List;

public interface SemesterScheduleService {
    SemesterSchedule createSchedule(SemesterSchedule schedule);
    SemesterSchedule updateSchedule(Long id, SemesterSchedule schedule);
    void deleteSchedule(Long id);
    SemesterSchedule findById(Long id);
    List<SemesterSchedule> findAll();
    List<SemesterSchedule> findActiveSchedules();
    boolean isScoreEntryAllowed(String semester, int year, String className);
    SemesterSchedule findActiveScheduleForClass(String semester, int year, String className);
    void lockExpiredSchedules();
    List<SemesterSchedule> findByYearAndSemester(int year, String semester);
}
