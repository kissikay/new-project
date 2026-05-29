package com.academic.service;

public class GradeCalculator {
    public static String calculateGrade(double score) {
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        if (score >= 50) return "D";
        return "F";
    }

    public static String calculateRemark(double score) {
        if (score >= 80) return "Excellent";
        if (score >= 70) return "Very Good";
        if (score >= 60) return "Good";
        if (score >= 50) return "Pass";
        return "Trail";
    }
}
