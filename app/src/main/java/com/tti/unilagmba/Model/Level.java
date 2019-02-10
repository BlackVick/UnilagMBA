package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 3/15/2018.
 */

public class Level {
    private String name;
    private String departmentId;

    public Level() {
    }

    public Level(String name, String departmentId) {
        this.name = name;
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }
}
