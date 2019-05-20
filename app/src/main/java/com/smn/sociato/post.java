package com.smn.sociato;

/**
 * Created by Dell on 01-10-2017.
 */

public class post {
    private String postProfile,postUsername, postDetails,postId, postImage, postTime, postLike;
    private int postTotalLike,postTotalComment;
    public post() {
    }

    public post(String postProfile, String postImage, String postUsername, String postDetails, String postId, String postTime, String postLike, int postTotalLike, int postTotalComment) {
        this.postProfile=postProfile;
        this.postImage=postImage;
        this.postUsername=postUsername;
        this.postDetails=postDetails;
        this.postId=postId;
        this.postTime=postTime;
        this.postLike=postLike;
        this.postTotalLike=postTotalLike;
        this.postTotalComment=postTotalComment;
    }

    public String getPostProfile() {
        return postProfile;
    }

    public void setPostProfile(String postProfile) {
        this.postProfile = postProfile;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostUsername() {
        return postUsername;
    }

    public void setOfferImage(String postUsername) {
        this.postUsername = postUsername;
    }

    public String getPostDetails() {
        return postDetails;
    }

    public void setPostDetails(String postDetails) {
        this.postDetails = postDetails;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPostLike() {
        return postLike;
    }

    public void setPostLike(String postLike) {
        this.postLike = postLike;
    }

    public int getPostTotalLike(){ return postTotalLike; }

    public void setPostTotalLike() { this.postTotalLike = postTotalLike; }

    public int getPostTotalComment(){ return postTotalComment; }

    public void setPostTotalComment() { this.postTotalComment = postTotalComment; }
}
