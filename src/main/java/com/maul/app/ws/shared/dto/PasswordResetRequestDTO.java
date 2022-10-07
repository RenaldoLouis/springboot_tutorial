package com.maul.app.ws.shared.dto;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.maul.app.ws.io.entity.UserEntity;

public class PasswordResetRequestDTO {
    private String token;

    private String userData; // test to prove that what DTO have doesnt have to be exact same with Response
                             // class

    @OneToOne()
    @JoinColumn(name = "users_id")
    private UserEntity userDetails; // we need userId which we will get from userEntity class

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }
}
