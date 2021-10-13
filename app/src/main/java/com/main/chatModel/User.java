package com.main.chatModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public String userName , userBio , phone_number , userID , profilePicturePath = "" , userRatings = "" , userDescription = "" , userEmail = "" , userPassword = "" , fcmToken;
    public Service service_offered = new Service();

    public User() {}

    public User(String userName, String userBio, String phone_number, String userID, String profilePicturePath, String userRatings, String userDescription, String userEmail, String userPassword, Service service_offered , String fcmToken) {
        this.userName = userName;
        this.userBio = userBio;
        this.phone_number = phone_number;
        this.userID = userID;
        this.profilePicturePath = profilePicturePath;
        this.userRatings = userRatings;
        this.userDescription = userDescription;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.service_offered = service_offered;
        this.fcmToken = fcmToken;
    }
}
