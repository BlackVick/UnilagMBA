package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 5/26/2018.
 */

public class Assignment {

    private String assignmentName;
    private String assignmentLecturer;
    private String assignmentCourse;
    private String assignmentDeadline;
    private String assignmentInstructions;
    private String assignmentFile;
    private String levelId;
    private String returnEmail;

    public Assignment() {
    }

    public Assignment(String assignmentName, String assignmentLecturer, String assignmentCourse, String assignmentDeadline, String assignmentInstructions, String assignmentFile, String levelId, String returnEmail) {
        this.assignmentName = assignmentName;
        this.assignmentLecturer = assignmentLecturer;
        this.assignmentCourse = assignmentCourse;
        this.assignmentDeadline = assignmentDeadline;
        this.assignmentInstructions = assignmentInstructions;
        this.assignmentFile = assignmentFile;
        this.levelId = levelId;
        this.returnEmail = returnEmail;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getAssignmentLecturer() {
        return assignmentLecturer;
    }

    public void setAssignmentLecturer(String assignmentLecturer) {
        this.assignmentLecturer = assignmentLecturer;
    }

    public String getAssignmentCourse() {
        return assignmentCourse;
    }

    public void setAssignmentCourse(String assignmentCourse) {
        this.assignmentCourse = assignmentCourse;
    }

    public String getAssignmentDeadline() {
        return assignmentDeadline;
    }

    public void setAssignmentDeadline(String assignmentDeadline) {
        this.assignmentDeadline = assignmentDeadline;
    }

    public String getAssignmentInstructions() {
        return assignmentInstructions;
    }

    public void setAssignmentInstructions(String assignmentInstructions) {
        this.assignmentInstructions = assignmentInstructions;
    }

    public String getAssignmentFile() {
        return assignmentFile;
    }

    public void setAssignmentFile(String assignmentFile) {
        this.assignmentFile = assignmentFile;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getReturnEmail() {
        return returnEmail;
    }

    public void setReturnEmail(String returnEmail) {
        this.returnEmail = returnEmail;
    }
}
