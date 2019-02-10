package com.tti.unilagmba.Model;

public class SpecializationCourse {

    private String name;
    private String courseId;
    private String specializationId;

    public SpecializationCourse() {
    }

    public SpecializationCourse(String name, String courseId, String specializationId) {
        this.name = name;
        this.courseId = courseId;
        this.specializationId = specializationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSpecializationId() {
        return specializationId;
    }

    public void setSpecializationId(String specializationId) {
        this.specializationId = specializationId;
    }
}
