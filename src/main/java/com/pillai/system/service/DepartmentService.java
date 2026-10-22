package com.pillai.system.service;

import com.pillai.system.dto.DepartmentDto;
import com.pillai.system.entity.Department;
import com.pillai.system.exception.DuplicateResourceException;
import com.pillai.system.exception.ResourceNotFoundException;
import com.pillai.system.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DepartmentDto getDepartmentById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return convertToDto(dept);
    }

    public DepartmentDto createDepartment(DepartmentDto dto) {
        if (departmentRepository.existsByDepartmentName(dto.getDepartmentName())) {
            throw new DuplicateResourceException("Department with name '" + dto.getDepartmentName() + "' already exists");
        }
        Department dept = new Department(dto.getDepartmentName(), dto.getDescription());
        departmentRepository.save(dept);
        return convertToDto(dept);
    }

    public DepartmentDto updateDepartment(Long id, DepartmentDto dto) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        if (!dept.getDepartmentName().equalsIgnoreCase(dto.getDepartmentName()) &&
                departmentRepository.existsByDepartmentName(dto.getDepartmentName())) {
            throw new DuplicateResourceException("Department with name '" + dto.getDepartmentName() + "' already exists");
        }

        dept.setDepartmentName(dto.getDepartmentName());
        dept.setDescription(dto.getDescription());
        departmentRepository.save(dept);
        return convertToDto(dept);
    }

    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private DepartmentDto convertToDto(Department dept) {
        return new DepartmentDto(dept.getId(), dept.getDepartmentName(), dept.getDescription());
    }
}
