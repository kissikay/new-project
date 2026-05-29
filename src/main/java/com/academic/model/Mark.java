package com.academic.model;

public class Mark {
    private int id;
    private int userId;
    private String courseCode;
    private String courseName;
    private int creditHours;
    private double score;
    private String grade;
    private String remark;

    public Mark(int id, int userId, String courseCode, String courseName, int creditHours, double score, String grade, String remark) {
        this.id = id;
        this.userId = userId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditHours = creditHours;
        this.score = score;
        this.grade = grade;
        this.remark = remark;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCreditHours() { return creditHours; }
    public double getScore() { return score; }
    public String getGrade() { return grade; }
    public String getRemark() { return remark; }
}
