package com.pillai.system.service;

import com.pillai.system.dto.SubjectDto;
import com.pillai.system.entity.SchoolClass;
import com.pillai.system.entity.Subject;
import com.pillai.system.entity.Teacher;
import com.pillai.system.exception.DuplicateResourceException;
import com.pillai.system.exception.ResourceNotFoundException;
import com.pillai.system.repository.ClassRepository;
import com.pillai.system.repository.SubjectRepository;
import com.pillai.system.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SubjectDto getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
        return convertToDto(subject);
    }

    public List<SubjectDto> getSubjectsByClassId(Long classId) {
        return subjectRepository.findBySchoolClassId(classId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<SubjectDto> getSubjectsByTeacherId(Long teacherId) {
        return subjectRepository.findByTeacherId(teacherId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SubjectDto createSubject(SubjectDto dto) {
        if (subjectRepository.existsBySubjectCode(dto.getSubjectCode())) {
            throw new DuplicateResourceException("Subject code already exists: " + dto.getSubjectCode());
        }

        SchoolClass schoolClass = null;
        if (dto.getClassId() != null) {
            schoolClass = classRepository.findById(dto.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + dto.getClassId()));
        }

        Teacher teacher = null;
        if (dto.getTeacherId() != null) {
            teacher = teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + dto.getTeacherId()));
        }

        Subject subject = new Subject(dto.getSubjectCode(), dto.getSubjectName(), dto.getCredits(), schoolClass, teacher);
        subjectRepository.save(subject);
        return convertToDto(subject);
    }

    public SubjectDto updateSubject(Long id, SubjectDto dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        if (!subject.getSubjectCode().equalsIgnoreCase(dto.getSubjectCode()) && subjectRepository.existsBySubjectCode(dto.getSubjectCode())) {
            throw new DuplicateResourceException("Subject code already exists: " + dto.getSubjectCode());
        }

        SchoolClass schoolClass = null;
        if (dto.getClassId() != null) {
            schoolClass = classRepository.findById(dto.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + dto.getClassId()));
        }

        Teacher teacher = null;
        if (dto.getTeacherId() != null) {
            teacher = teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + dto.getTeacherId()));
        }

        subject.setSubjectCode(dto.getSubjectCode());
        subject.setSubjectName(dto.getSubjectName());
        subject.setCredits(dto.getCredits());
        subject.setSchoolClass(schoolClass);
        subject.setTeacher(teacher);

        subjectRepository.save(subject);
        return convertToDto(subject);
    }

    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }

    public SubjectDto convertToDto(Subject subject) {
        return new SubjectDto(
                subject.getId(),
                subject.getSubjectCode(),
                subject.getSubjectName(),
                subject.getCredits(),
                subject.getSchoolClass() != null ? subject.getSchoolClass().getId() : null,
                subject.getSchoolClass() != null ? subject.getSchoolClass().getClassName() : null,
                subject.getTeacher() != null ? subject.getTeacher().getId() : null,
                subject.getTeacher() != null ? subject.getTeacher().getName() : null
        );
    }
}
