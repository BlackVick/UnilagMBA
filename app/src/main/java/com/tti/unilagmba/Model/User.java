package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 3/9/2018.
 */

public class User {
    private String matric;
    private String name;
    private String userName;
    private String mail;
    private String number;
    private String occupation;
    private String profilePicture;
    private String profilePictureThumb;
    private String status;
    private String password;
    private String secureCode;
    private String userType;

    public User() {
    }

    public User(String name, String userName, String mail, String number, String occupation, String profilePicture, String profilePictureThumb, String status, String password, String secureCode, String userType) {
        this.name = name;
        this.userName = userName;
        this.mail = mail;
        this.number = number;
        this.occupation = occupation;
        this.profilePicture = profilePicture;
        this.profilePictureThumb = profilePictureThumb;
        this.status = status;
        this.password = password;
        this.secureCode = secureCode;
        this.userType = userType;
    }

    public User(String name, String userName, String profilePicture, String profilePictureThumb, String status) {
        this.name = name;
        this.userName = userName;
        this.profilePicture = profilePicture;
        this.profilePictureThumb = profilePictureThumb;
        this.status = status;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getMatric() {
        return matric;
    }

    public void setMatric(String matric) {
        this.matric = matric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
