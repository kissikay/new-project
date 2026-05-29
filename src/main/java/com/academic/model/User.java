package com.academic.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String indexNumber;
    private String programme;
    private int level;
    private int semester;
    private int courseCount;

    public User(int id, String username, String password, String indexNumber, String programme, int level, int semester, int courseCount) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.indexNumber = indexNumber;
        this.programme = programme;
        this.level = level;
        this.semester = semester;
        this.courseCount = courseCount;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getIndexNumber() { return indexNumber; }
    public String getProgramme() { return programme; }
    public int getLevel() { return level; }
    public int getSemester() { return semester; }
    public int getCourseCount() { return courseCount; }
}
