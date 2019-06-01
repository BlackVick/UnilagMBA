package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 3/10/2018.
 */

public class NewsFeeds {
    private String newsTitle;
    private String sender;
    private String newsDetail;
    private String newsImage;
    private String time;

    public NewsFeeds() {
    }


    public NewsFeeds(String newsTitle, String sender, String newsDetail, String newsImage, String time) {
        this.newsTitle = newsTitle;
        this.sender = sender;
        this.newsDetail = newsDetail;
        this.newsImage = newsImage;
        this.time = time;
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
}
