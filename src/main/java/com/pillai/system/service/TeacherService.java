package com.pillai.system.service;

import com.pillai.system.dto.TeacherDto;
import com.pillai.system.entity.Department;
import com.pillai.system.entity.Role;
import com.pillai.system.entity.Teacher;
import com.pillai.system.entity.User;
import com.pillai.system.exception.DuplicateResourceException;
import com.pillai.system.exception.ResourceNotFoundException;
import com.pillai.system.repository.DepartmentRepository;
import com.pillai.system.repository.TeacherRepository;
import com.pillai.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<TeacherDto> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TeacherDto getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        return convertToDto(teacher);
    }

    @Transactional
    public TeacherDto createTeacher(TeacherDto dto) {
        if (teacherRepository.existsByTeacherId(dto.getTeacherId())) {
            throw new DuplicateResourceException("Teacher ID already exists: " + dto.getTeacherId());
        }
        if (teacherRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Teacher Email already exists: " + dto.getEmail());
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
        }

        // Create User for login if doesn't exist
        String username = dto.getTeacherId().toLowerCase();
        User user;
        if (userRepository.existsByUsername(username)) {
            user = userRepository.findByUsername(username).orElse(null);
        } else {
            user = new User(username, dto.getEmail(), passwordEncoder.encode("teacher123"), Role.TEACHER);
            userRepository.save(user);
        }

        Teacher teacher = new Teacher(
                dto.getTeacherId(),
                dto.getName(),
                dto.getEmail(),
                dto.getPhone(),
                department,
                user
        );
        teacherRepository.save(teacher);
        return convertToDto(teacher);
    }

    @Transactional
    public TeacherDto updateTeacher(Long id, TeacherDto dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        if (!teacher.getTeacherId().equalsIgnoreCase(dto.getTeacherId()) && teacherRepository.existsByTeacherId(dto.getTeacherId())) {
            throw new DuplicateResourceException("Teacher ID already exists: " + dto.getTeacherId());
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
        }

        teacher.setTeacherId(dto.getTeacherId());
        teacher.setName(dto.getName());
        teacher.setEmail(dto.getEmail());
        teacher.setPhone(dto.getPhone());
        teacher.setDepartment(department);

        if (teacher.getUser() != null) {
            teacher.getUser().setEmail(dto.getEmail());
            userRepository.save(teacher.getUser());
        }

        teacherRepository.save(teacher);
        return convertToDto(teacher);
    }

    @Transactional
    public TeacherDto patchTeacher(Long id, TeacherDto dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        if (dto.getName() != null) teacher.setName(dto.getName());
        if (dto.getPhone() != null) teacher.setPhone(dto.getPhone());
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            teacher.setDepartment(department);
        }

        teacherRepository.save(teacher);
        return convertToDto(teacher);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        User user = teacher.getUser();
        teacherRepository.delete(teacher);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public TeacherDto convertToDto(Teacher teacher) {
        return new TeacherDto(
                teacher.getId(),
                teacher.getTeacherId(),
                teacher.getName(),
                teacher.getEmail(),
                teacher.getPhone(),
                teacher.getDepartment() != null ? teacher.getDepartment().getId() : null,
                teacher.getDepartment() != null ? teacher.getDepartment().getDepartmentName() : null,
                teacher.getUser() != null ? teacher.getUser().getId() : null
        );
    }
}
