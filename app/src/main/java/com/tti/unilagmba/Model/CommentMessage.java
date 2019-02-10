package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 3/14/2018.
 */

public class CommentMessage {
    private String comment;
    private String userMatric;
    private String user;
    private String commentTime;
    private String newsId;

    public CommentMessage() {
    }

    public CommentMessage(String comment, String userMatric, String user, String commentTime, String newsId) {
        this.comment = comment;
        this.userMatric = userMatric;
        this.user = user;
        this.commentTime = commentTime;
        this.newsId = newsId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserMatric() {
        return userMatric;
    }

    public void setUserMatric(String userMatric) {
        this.userMatric = userMatric;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }
}
