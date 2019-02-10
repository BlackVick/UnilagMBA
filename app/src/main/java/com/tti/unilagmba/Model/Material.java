package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 3/10/2018.
 */

public class Material {

    private String name;
    private String documentLink;
    private String levelId;
    private String documentType;
    private String courseName;
    private String materialInfo;

    public Material() {
    }

    public Material(String name, String documentLink, String levelId, String documentType, String courseName, String materialInfo) {
        this.name = name;
        this.documentLink = documentLink;
        this.levelId = levelId;
        this.documentType = documentType;
        this.courseName = courseName;
        this.materialInfo = materialInfo;
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

    public String getMaterialInfo() {
        return materialInfo;
    }

    public void setMaterialInfo(String materialInfo) {
        this.materialInfo = materialInfo;
    }
}
