package org.example;

public class Course {
    private int courseId;
    private String courseName;
    private int instructorId;

    public Course(int courseId, String courseName, int instructorId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructorId = instructorId;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getInstructorId() {
        return instructorId;
    }
}

