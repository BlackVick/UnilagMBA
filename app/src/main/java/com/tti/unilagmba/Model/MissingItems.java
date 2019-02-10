package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 3/29/2018.
 */

public class MissingItems {
    private String newsTitle;
    private String sender;
    private String newsDetail;
    private String newsImage;
    private String time;
    private String newsCategory;

    public MissingItems() {
    }

    public MissingItems(String newsTitle, String sender, String newsDetail, String newsImage, String time, String newsCategory) {
        this.newsTitle = newsTitle;
        this.sender = sender;
        this.newsDetail = newsDetail;
        this.newsImage = newsImage;
        this.time = time;
        this.newsCategory = newsCategory;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getNewsDetail() {
        return newsDetail;
    }

    public void setNewsDetail(String newsDetail) {
        this.newsDetail = newsDetail;
    }

    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNewsCategory() {
        return newsCategory;
    }

    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
    }
}
