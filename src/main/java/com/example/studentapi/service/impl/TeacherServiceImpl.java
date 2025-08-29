package com.example.studentapi.service.impl;

import com.example.studentapi.model.Teacher;
import com.example.studentapi.repository.TeacherRepository;
import com.example.studentapi.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public Teacher findById(Long id) {
        return teacherRepository.findById(id).orElse(null);
    }

    @Override
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Override
    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    public Teacher update(Long id, Teacher teacher) {
        if (teacherRepository.existsById(id)) {
            teacher.setId(id);
            return teacherRepository.save(teacher);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        teacherRepository.deleteById(id);
    }
}