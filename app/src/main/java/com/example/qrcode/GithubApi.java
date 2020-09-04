package com.example.qrcode;

import com.example.qrcode.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GithubApi {

    @GET("user")
    Call<User> getUser(
            @Header("Authorization") String authHeader
    );

}
