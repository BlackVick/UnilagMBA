package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 6/26/2018.
 */

public class FacultyAdminModel {

    private String email;
    private String name;
    private String phone;
    private String post;
    private String profilePicture;
    private String profilePictureThumb;
    private String officeLocation;

    public FacultyAdminModel() {
    }

    public FacultyAdminModel(String email, String name, String phone, String post, String profilePicture, String profilePictureThumb, String officeLocation) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.post = post;
        this.profilePicture = profilePicture;
        this.profilePictureThumb = profilePictureThumb;
        this.officeLocation = officeLocation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePictureThumb() {
        return profilePictureThumb;
    }

    public void setProfilePictureThumb(String profilePictureThumb) {
        this.profilePictureThumb = profilePictureThumb;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
}
