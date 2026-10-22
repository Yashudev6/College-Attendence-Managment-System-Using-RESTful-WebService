package com.pillai.system.service;

import org.springframework.stereotype.Service;

@Service
public class GradingService {

    /**
     * Centralized configurable grading calculation
     * 90 - 100 => A+
     * 80 - 89  => A
     * 70 - 79  => B+
     * 60 - 69  => B
     * 50 - 59  => C
     * 40 - 49  => D
     * Below 40 => F
     */
    public String calculateGrade(double percentage) {
        if (percentage >= 90.0) {
            return "A+";
        } else if (percentage >= 80.0) {
            return "A";
        } else if (percentage >= 70.0) {
            return "B+";
        } else if (percentage >= 60.0) {
            return "B";
        } else if (percentage >= 50.0) {
            return "C";
        } else if (percentage >= 40.0) {
            return "D";
        } else {
            return "F";
        }
    }
}
