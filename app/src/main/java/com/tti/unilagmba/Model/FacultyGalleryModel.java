package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 6/26/2018.
 */

public class FacultyGalleryModel {

    private String facultyId;
    private String galleryImage;
    private String galleryInfo;

    public FacultyGalleryModel() {
    }

    public FacultyGalleryModel(String facultyId, String galleryImage, String galleryInfo) {
        this.facultyId = facultyId;
        this.galleryImage = galleryImage;
        this.galleryInfo = galleryInfo;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getGalleryImage() {
        return galleryImage;
    }

    public void setGalleryImage(String galleryImage) {
        this.galleryImage = galleryImage;
    }

    public String getGalleryInfo() {
        return galleryInfo;
    }

    public void setGalleryInfo(String galleryInfo) {
        this.galleryInfo = galleryInfo;
    }
}
