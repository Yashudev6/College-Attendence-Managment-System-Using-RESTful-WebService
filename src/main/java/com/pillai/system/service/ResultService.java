package com.pillai.system.service;

import com.pillai.system.dto.ResultDto;
import com.pillai.system.dto.ResultRequestDto;
import com.pillai.system.dto.StudentResultSummaryDto;
import com.pillai.system.entity.Result;
import com.pillai.system.entity.Student;
import com.pillai.system.entity.Subject;
import com.pillai.system.exception.BadRequestException;
import com.pillai.system.exception.ResourceNotFoundException;
import com.pillai.system.repository.ResultRepository;
import com.pillai.system.repository.StudentRepository;
import com.pillai.system.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GradingService gradingService;

    public List<ResultDto> getAllResults() {
        return resultRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ResultDto getResultById(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result record not found with id: " + id));
        return convertToDto(result);
    }

    public List<ResultDto> getResultsByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return resultRepository.findByStudentId(studentId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ResultDto> getResultsBySubjectId(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Subject not found with id: " + subjectId);
        }
        return resultRepository.findBySubjectId(subjectId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResultDto enterMarks(ResultRequestDto requestDto) {
        validateMarks(requestDto.getMarks(), requestDto.getMaxMarks());

        Student student = studentRepository.findById(requestDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + requestDto.getStudentId()));

        Subject subject = subjectRepository.findById(requestDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + requestDto.getSubjectId()));

        double percentage = (requestDto.getMarks() / requestDto.getMaxMarks()) * 100.0;
        String grade = gradingService.calculateGrade(percentage);

        Result result = new Result(
                student,
                subject,
                requestDto.getMarks(),
                requestDto.getMaxMarks(),
                requestDto.getSemester(),
                requestDto.getExamType(),
                grade,
                requestDto.getRemarks()
        );

        resultRepository.save(result);
        return convertToDto(result);
    }

    @Transactional
    public ResultDto updateResult(Long id, ResultRequestDto requestDto) {
        validateMarks(requestDto.getMarks(), requestDto.getMaxMarks());

        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result record not found with id: " + id));

        Student student = studentRepository.findById(requestDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + requestDto.getStudentId()));

        Subject subject = subjectRepository.findById(requestDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + requestDto.getSubjectId()));

        double percentage = (requestDto.getMarks() / requestDto.getMaxMarks()) * 100.0;
        String grade = gradingService.calculateGrade(percentage);

        result.setStudent(student);
        result.setSubject(subject);
        result.setMarks(requestDto.getMarks());
        result.setMaxMarks(requestDto.getMaxMarks());
        result.setSemester(requestDto.getSemester());
        result.setExamType(requestDto.getExamType());
        result.setGrade(grade);
        result.setRemarks(requestDto.getRemarks());

        resultRepository.save(result);
        return convertToDto(result);
    }

    @Transactional
    public ResultDto patchResult(Long id, Double marks, Double maxMarks, String remarks) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result record not found with id: " + id));

        Double newMarks = marks != null ? marks : result.getMarks();
        Double newMaxMarks = maxMarks != null ? maxMarks : result.getMaxMarks();
        validateMarks(newMarks, newMaxMarks);

        result.setMarks(newMarks);
        result.setMaxMarks(newMaxMarks);
        if (remarks != null) {
            result.setRemarks(remarks);
        }

        double percentage = (newMarks / newMaxMarks) * 100.0;
        result.setGrade(gradingService.calculateGrade(percentage));

        resultRepository.save(result);
        return convertToDto(result);
    }

    @Transactional
    public void deleteResult(Long id) {
        if (!resultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Result record not found with id: " + id);
        }
        resultRepository.deleteById(id);
    }

    public StudentResultSummaryDto calculateStudentResultSummary(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        List<Result> results = resultRepository.findByStudentId(studentId);
        List<ResultDto> dtoList = results.stream().map(this::convertToDto).collect(Collectors.toList());

        double totalMarks = 0.0;
        double totalMaxMarks = 0.0;

        for (Result r : results) {
            totalMarks += r.getMarks();
            totalMaxMarks += r.getMaxMarks();
        }

        double overallPercentage = totalMaxMarks > 0 ? (totalMarks / totalMaxMarks) * 100.0 : 0.0;
        overallPercentage = Math.round(overallPercentage * 100.0) / 100.0;

        String overallGrade = totalMaxMarks > 0 ? gradingService.calculateGrade(overallPercentage) : "N/A";
        String className = student.getSchoolClass() != null ? student.getSchoolClass().getClassName() : "N/A";

        return new StudentResultSummaryDto(
                student.getId(),
                student.getStudentId(),
                student.getName(),
                className,
                totalMarks,
                totalMaxMarks,
                overallPercentage,
                overallGrade,
                dtoList
        );
    }

    private void validateMarks(Double marks, Double maxMarks) {
        if (marks == null || maxMarks == null) {
            throw new BadRequestException("Marks and max marks are required");
        }
        if (marks < 0) {
            throw new BadRequestException("Marks cannot be negative");
        }
        if (maxMarks <= 0) {
            throw new BadRequestException("Max marks must be greater than zero");
        }
        if (marks > maxMarks) {
            throw new BadRequestException("Marks (" + marks + ") cannot exceed max marks (" + maxMarks + ")");
        }
    }

    public ResultDto convertToDto(Result result) {
        double percentage = (result.getMarks() / result.getMaxMarks()) * 100.0;
        percentage = Math.round(percentage * 100.0) / 100.0;

        return new ResultDto(
                result.getId(),
                result.getStudent().getId(),
                result.getStudent().getStudentId(),
                result.getStudent().getName(),
                result.getSubject().getId(),
                result.getSubject().getSubjectCode(),
                result.getSubject().getSubjectName(),
                result.getMarks(),
                result.getMaxMarks(),
                percentage,
                result.getSemester(),
                result.getExamType(),
                result.getGrade(),
                result.getRemarks()
        );
    }
}
