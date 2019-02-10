package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 6/22/2018.
 */

public class FriendRequest {
    private String requestType;

    public FriendRequest() {
    }

    public FriendRequest(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
