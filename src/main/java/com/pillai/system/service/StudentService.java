package com.pillai.system.service;

import com.pillai.system.dto.StudentDto;
import com.pillai.system.entity.*;
import com.pillai.system.exception.DuplicateResourceException;
import com.pillai.system.exception.ResourceNotFoundException;
import com.pillai.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return convertToDto(student);
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentByUsername(String username) {
        Student student = studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + username));
        return convertToDto(student);
    }

    @Transactional(readOnly = true)
    public List<StudentDto> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentRepository.searchStudents(query.trim()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByClassId(Long classId) {
        return studentRepository.findBySchoolClassId(classId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentDto createStudent(StudentDto dto) {
        if (studentRepository.existsByStudentId(dto.getStudentId())) {
            throw new DuplicateResourceException("Student ID already exists: " + dto.getStudentId());
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Student Email already exists: " + dto.getEmail());
        }

        SchoolClass schoolClass = null;
        if (dto.getClassId() != null) {
            schoolClass = classRepository.findById(dto.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + dto.getClassId()));
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
        } else if (schoolClass != null && schoolClass.getDepartment() != null) {
            department = schoolClass.getDepartment();
        }

        // Create User account for login
        String username = dto.getStudentId().toLowerCase();
        User user;
        if (userRepository.existsByUsername(username)) {
            user = userRepository.findByUsername(username).orElse(null);
        } else {
            user = new User(username, dto.getEmail(), passwordEncoder.encode("student123"), Role.STUDENT);
            userRepository.save(user);
        }

        Student student = new Student(
                dto.getStudentId(),
                dto.getName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getDateOfBirth(),
                dto.getGender(),
                dto.getAddress(),
                schoolClass,
                department,
                user
        );
        studentRepository.save(student);
        return convertToDto(student);
    }

    @Transactional
    public StudentDto updateStudent(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        if (!student.getStudentId().equalsIgnoreCase(dto.getStudentId()) && studentRepository.existsByStudentId(dto.getStudentId())) {
            throw new DuplicateResourceException("Student ID already exists: " + dto.getStudentId());
        }

        SchoolClass schoolClass = null;
        if (dto.getClassId() != null) {
            schoolClass = classRepository.findById(dto.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + dto.getClassId()));
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
        } else if (schoolClass != null && schoolClass.getDepartment() != null) {
            department = schoolClass.getDepartment();
        }

        student.setStudentId(dto.getStudentId());
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(dto.getGender());
        student.setAddress(dto.getAddress());
        student.setSchoolClass(schoolClass);
        student.setDepartment(department);

        if (student.getUser() != null) {
            student.getUser().setEmail(dto.getEmail());
            userRepository.save(student.getUser());
        }

        studentRepository.save(student);
        return convertToDto(student);
    }

    @Transactional
    public StudentDto patchStudent(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        if (dto.getName() != null) student.setName(dto.getName());
        if (dto.getPhone() != null) student.setPhone(dto.getPhone());
        if (dto.getAddress() != null) student.setAddress(dto.getAddress());
        if (dto.getGender() != null) student.setGender(dto.getGender());
        if (dto.getDateOfBirth() != null) student.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getClassId() != null) {
            SchoolClass schoolClass = classRepository.findById(dto.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + dto.getClassId()));
            student.setSchoolClass(schoolClass);
        }
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            student.setDepartment(department);
        }

        studentRepository.save(student);
        return convertToDto(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        User user = student.getUser();
        studentRepository.delete(student);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public StudentDto convertToDto(Student student) {
        return new StudentDto(
                student.getId(),
                student.getStudentId(),
                student.getName(),
                student.getEmail(),
                student.getPhone(),
                student.getDateOfBirth(),
                student.getGender(),
                student.getAddress(),
                student.getSchoolClass() != null ? student.getSchoolClass().getId() : null,
                student.getSchoolClass() != null ? student.getSchoolClass().getClassName() : null,
                student.getDepartment() != null ? student.getDepartment().getId() : null,
                student.getDepartment() != null ? student.getDepartment().getDepartmentName() : null,
                student.getUser() != null ? student.getUser().getId() : null
        );
    }
}
