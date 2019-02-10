package com.tti.unilagmba.Model;

/**
 * Created by Scarecrow on 7/14/2018.
 */

public class VotingCandidates {

    private String name;
    private String thumb;

    public VotingCandidates() {
    }

    public VotingCandidates(String name, String thumb) {
        this.name = name;
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
