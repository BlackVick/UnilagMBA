package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 4/13/2018.
 */

public class Todo {


    private String day;
    private String courseCode;
    private String venue;
    private String start;
    private String stop;

    public Todo() {
    }

    public Todo(String day, String courseCode, String venue, String start, String stop) {
        this.day = day;
        this.courseCode = courseCode;
        this.venue = venue;
        this.start = start;
        this.stop = stop;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }
}
