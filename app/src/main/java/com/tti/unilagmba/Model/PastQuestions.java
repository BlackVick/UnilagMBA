package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 7/12/2018.
 */

public class PastQuestions {
    private String name;
    private String documentLink;
    private String levelId;
    private String documentType;
    private String courseName;

    public PastQuestions() {
    }

    public PastQuestions(String name, String documentLink, String levelId, String documentType, String courseName) {
        this.name = name;
        this.documentLink = documentLink;
        this.levelId = levelId;
        this.documentType = documentType;
        this.courseName = courseName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentLink() {
        return documentLink;
    }

    public void setDocumentLink(String documentLink) {
        this.documentLink = documentLink;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
