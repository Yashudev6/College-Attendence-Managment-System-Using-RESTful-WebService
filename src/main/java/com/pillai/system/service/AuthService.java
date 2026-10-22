package com.pillai.system.service;

import com.pillai.system.dto.AuthRequest;
import com.pillai.system.dto.AuthResponse;
import com.pillai.system.dto.RegisterRequest;
import com.pillai.system.entity.*;
import com.pillai.system.exception.BadRequestException;
import com.pillai.system.exception.DuplicateResourceException;
import com.pillai.system.exception.ResourceNotFoundException;
import com.pillai.system.repository.*;
import com.pillai.system.security.JwtUtils;
import com.pillai.system.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Long entityId = null;
        String name = userDetails.getUsername();

        if (userDetails.getRole() == Role.STUDENT) {
            Optional<Student> student = studentRepository.findByUserId(userDetails.getId());
            if (student.isPresent()) {
                entityId = student.get().getId();
                name = student.get().getName();
            }
        } else if (userDetails.getRole() == Role.TEACHER) {
            Optional<Teacher> teacher = teacherRepository.findByUserId(userDetails.getId());
            if (teacher.isPresent()) {
                entityId = teacher.get().getId();
                name = teacher.get().getName();
            }
        } else if (userDetails.getRole() == Role.ADMIN) {
            Optional<Admin> admin = adminRepository.findByUserId(userDetails.getId());
            if (admin.isPresent()) {
                entityId = admin.get().getId();
                name = admin.get().getName();
            }
        }

        return new AuthResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getRole(), entityId, name);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already in use: " + request.getEmail());
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );
        userRepository.save(user);

        Long entityId = null;

        if (request.getRole() == Role.STUDENT) {
            String studentIdStr = request.getStudentIdStr() != null ? request.getStudentIdStr() : "STU" + System.currentTimeMillis() % 10000;
            if (studentRepository.existsByStudentId(studentIdStr)) {
                throw new DuplicateResourceException("Student ID already exists: " + studentIdStr);
            }

            SchoolClass schoolClass = null;
            if (request.getClassId() != null) {
                schoolClass = classRepository.findById(request.getClassId())
                        .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));
            }

            Department department = null;
            if (request.getDepartmentId() != null) {
                department = departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
            }

            Student student = new Student(
                    studentIdStr,
                    request.getName(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getDateOfBirth(),
                    request.getGender(),
                    request.getAddress(),
                    schoolClass,
                    department,
                    user
            );
            studentRepository.save(student);
            entityId = student.getId();

        } else if (request.getRole() == Role.TEACHER) {
            String teacherIdStr = request.getTeacherIdStr() != null ? request.getTeacherIdStr() : "TCH" + System.currentTimeMillis() % 10000;
            if (teacherRepository.existsByTeacherId(teacherIdStr)) {
                throw new DuplicateResourceException("Teacher ID already exists: " + teacherIdStr);
            }

            Department department = null;
            if (request.getDepartmentId() != null) {
                department = departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
            }

            Teacher teacher = new Teacher(
                    teacherIdStr,
                    request.getName(),
                    request.getEmail(),
                    request.getPhone(),
                    department,
                    user
            );
            teacherRepository.save(teacher);
            entityId = teacher.getId();

        } else if (request.getRole() == Role.ADMIN) {
            Admin admin = new Admin(request.getName(), request.getEmail(), user);
            adminRepository.save(admin);
            entityId = admin.getId();
        }

        String token = jwtUtils.generateTokenFromUsername(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole(), entityId, request.getName());
    }
}
