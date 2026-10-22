package com.pillai.system;

import com.pillai.system.dto.ResultDto;
import com.pillai.system.dto.ResultRequestDto;
import com.pillai.system.service.GradingService;
import com.pillai.system.service.ResultService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ResultServiceTest {

    @Autowired
    private ResultService resultService;

    @Autowired
    private GradingService gradingService;

    @Test
    void testGradingServiceCalculation() {
        Assertions.assertEquals("A+", gradingService.calculateGrade(95.0));
        Assertions.assertEquals("A", gradingService.calculateGrade(84.0));
        Assertions.assertEquals("B+", gradingService.calculateGrade(75.0));
        Assertions.assertEquals("B", gradingService.calculateGrade(62.0));
        Assertions.assertEquals("C", gradingService.calculateGrade(55.0));
        Assertions.assertEquals("D", gradingService.calculateGrade(45.0));
        Assertions.assertEquals("F", gradingService.calculateGrade(32.0));
    }

    @Test
    void testEnterMarksAndGradeAssignment() {
        ResultRequestDto request = new ResultRequestDto(1L, 1L, 94.0, 100.0, "Semester 2", "Final Exam", "Outstanding");
        ResultDto created = resultService.enterMarks(request);

        Assertions.assertNotNull(created);
        Assertions.assertEquals(94.0, created.getMarks());
        Assertions.assertEquals("A+", created.getGrade());
    }
}
