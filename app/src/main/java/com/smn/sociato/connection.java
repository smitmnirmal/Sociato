package com.smn.sociato;

/**
 * Created by Dell on 04-10-2017.
 */

public class connection {
    private String connectionProfile,connectionUsername,connectionRequestDetails,connectionUserId,connectionMessage;

    public connection(){

    }
    public connection(String connectionProfile, String connectionUsername, String connectionRequestDetails, String connectionUserId, String connectionMessage) {
        this.connectionProfile=connectionProfile;
        this.connectionUsername=connectionUsername;
        this.connectionRequestDetails=connectionRequestDetails;
        this.connectionUserId=connectionUserId;
        this.connectionMessage=connectionMessage;
    }

    public String getConnectionProfile() {
        return connectionProfile;
    }

    public void setConnectionProfile(String connectionProfile) {
        this.connectionProfile= connectionProfile;
    }

    public String getConnectionUsername() {
        return connectionUsername;
    }

    public void setConnectionUsername(String connectionUsername) {
        this.connectionUsername= connectionUsername;
    }

    public String getConnectionRequestDetails() {
        return connectionRequestDetails;
    }

    public void setConnectionRequestDetails(String connectionRequestDetails) {
        this.connectionRequestDetails = connectionRequestDetails;
    }

    public String getConnectionUserId() {
        return connectionUserId;
    }

    public void setConnectionUserId(String connectionUserId) {
        this.connectionUserId = connectionUserId;
    }

    public String getConnectionMessage() {
        return connectionMessage;
    }

    public void setConnectionMessage(String connectionMessage) {
        this.connectionMessage = connectionMessage;
    }
}