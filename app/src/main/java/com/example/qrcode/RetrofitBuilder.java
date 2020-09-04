package com.example.qrcode;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    private static GithubApi githubApi;

    private static GithubApi buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubApi.class);
    }

    public static GithubApi getService() {
        if (githubApi == null) {
            githubApi = buildRetrofit();
        }
        return githubApi;
    }
}
