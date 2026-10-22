package com.pillai.system;

import com.pillai.system.dto.StudentDto;
import com.pillai.system.service.StudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Test
    void testGetAllStudents() {
        List<StudentDto> students = studentService.getAllStudents();
        Assertions.assertNotNull(students);
        Assertions.assertTrue(students.size() >= 10, "Initial seed should have at least 10 students");
    }

    @Test
    void testCreateAndRetrieveStudent() {
        StudentDto newStudent = new StudentDto();
        newStudent.setStudentId("STU_TEST_99");
        newStudent.setName("Unit Test Student");
        newStudent.setEmail("unittest99@pillai.edu");
        newStudent.setPhone("+91 99999 88888");

        StudentDto created = studentService.createStudent(newStudent);
        Assertions.assertNotNull(created);
        Assertions.assertEquals("STU_TEST_99", created.getStudentId());

        StudentDto fetched = studentService.getStudentById(created.getId());
        Assertions.assertNotNull(fetched);
        Assertions.assertEquals("Unit Test Student", fetched.getName());
    }
}
