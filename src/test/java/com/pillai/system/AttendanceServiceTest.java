package com.pillai.system;

import com.pillai.system.dto.AttendanceDto;
import com.pillai.system.dto.AttendanceRequestDto;
import com.pillai.system.dto.AttendanceSummaryDto;
import com.pillai.system.entity.AttendanceStatus;
import com.pillai.system.exception.DuplicateResourceException;
import com.pillai.system.service.AttendanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("test")
public class AttendanceServiceTest {

    @Autowired
    private AttendanceService attendanceService;

    @Test
    void testAttendanceCalculationFormula() {
        AttendanceSummaryDto summary = attendanceService.calculateAttendanceSummary(1L);
        Assertions.assertNotNull(summary);
        Assertions.assertTrue(summary.getTotalClasses() > 0);
        Assertions.assertTrue(summary.getAttendancePercentage() >= 0.0 && summary.getAttendancePercentage() <= 100.0);
    }

    @Test
    void testDuplicateAttendancePrevention() {
        LocalDate date = LocalDate.of(2028, 1, 15);
        AttendanceRequestDto dto1 = new AttendanceRequestDto(1L, 1L, date, AttendanceStatus.PRESENT, "First mark");
        attendanceService.markAttendance(dto1);

        // Submitting duplicate should throw DuplicateResourceException
        AttendanceRequestDto dto2 = new AttendanceRequestDto(1L, 1L, date, AttendanceStatus.PRESENT, "Duplicate mark");
        Assertions.assertThrows(DuplicateResourceException.class, () -> {
            attendanceService.markAttendance(dto2);
        });
    }
}
