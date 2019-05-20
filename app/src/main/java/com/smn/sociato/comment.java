package com.smn.sociato;

/**
 * Created by Dell on 07-10-2017.
 */

public class comment {
    private String commentUserProfile,commentUsername, commentDetails, commentId, commentTime;

    public comment() {
    }

    public comment(String commentUserProfile, String commentUsername, String commentDetails, String commentTime) {
        this.commentUserProfile=commentUserProfile;
        this.commentUsername=commentUsername;
        this.commentDetails=commentDetails;
        this.commentTime=commentTime;
    }

    public String getCommentUserProfile() {
        return commentUserProfile;
    }

    public void setCommentUserProfile(String commentUserProfile) {
        this.commentUserProfile = commentUserProfile;
    }

    public String getCommentUsername() {
        return commentUsername;
    }

    public void setCommentUsername(String commentUsername) {
        this.commentUsername = commentUsername;
    }

    public String getCommentDetails() {
        return commentDetails;
    }

    public void setCommentDetails(String commentDetails) {
        this.commentDetails= commentDetails;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

}

