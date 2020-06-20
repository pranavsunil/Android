package com.login_signup_screendesign_demo.Model;

import com.google.gson.annotations.SerializedName;

public class BodyLoginUser {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public BodyLoginUser(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
