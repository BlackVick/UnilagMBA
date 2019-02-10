package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 3/15/2018.
 */

public class Department {

    private String name;
    private String facultyId;

    public Department() {
    }

    public Department(String name, String facultyId) {
        this.name = name;
        this.facultyId = facultyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }
}
