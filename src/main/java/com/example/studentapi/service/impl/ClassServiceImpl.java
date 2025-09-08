package com.example.studentapi.service.impl;

import com.example.studentapi.model.TeacherClassAssignment;
import com.example.studentapi.repository.TeacherClassAssignmentRepository;
import com.example.studentapi.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private TeacherClassAssignmentRepository teacherAssignmentRepository; // Fixed field name

    public boolean teacherHasAccessToClass(Long teacherId, Long schoolClassId) {
        return teacherAssignmentRepository.existsByTeacherIdAndSchoolClassIdAndIsActive(teacherId, schoolClassId);
    }

    public List<TeacherClassAssignment> getTeacherAssignments(Long teacherId) {
        return teacherAssignmentRepository.findActiveAssignmentsByTeacherId(teacherId);
    }

    public List<TeacherClassAssignment> getClassAssignments(Long schoolClassId) {
        return teacherAssignmentRepository.findActiveAssignmentsBySchoolClassId(schoolClassId);
    }
}