package com.pillai.system.service;

import com.pillai.system.dto.ClassDto;
import com.pillai.system.entity.Department;
import com.pillai.system.entity.SchoolClass;
import com.pillai.system.exception.ResourceNotFoundException;
import com.pillai.system.repository.ClassRepository;
import com.pillai.system.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<ClassDto> getAllClasses() {
        return classRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ClassDto getClassById(Long id) {
        SchoolClass schoolClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));
        return convertToDto(schoolClass);
    }

    public ClassDto createClass(ClassDto dto) {
        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));

        SchoolClass schoolClass = new SchoolClass(dto.getClassName(), dto.getSemester(), dept);
        classRepository.save(schoolClass);
        return convertToDto(schoolClass);
    }

    public ClassDto updateClass(Long id, ClassDto dto) {
        SchoolClass schoolClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));

        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));

        schoolClass.setClassName(dto.getClassName());
        schoolClass.setSemester(dto.getSemester());
        schoolClass.setDepartment(dept);
        classRepository.save(schoolClass);
        return convertToDto(schoolClass);
    }

    public void deleteClass(Long id) {
        if (!classRepository.existsById(id)) {
            throw new ResourceNotFoundException("Class not found with id: " + id);
        }
        classRepository.deleteById(id);
    }

    private ClassDto convertToDto(SchoolClass sc) {
        return new ClassDto(
                sc.getId(),
                sc.getClassName(),
                sc.getSemester(),
                sc.getDepartment() != null ? sc.getDepartment().getId() : null,
                sc.getDepartment() != null ? sc.getDepartment().getDepartmentName() : null
        );
    }
}
