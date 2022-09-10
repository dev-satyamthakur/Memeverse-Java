package com.codefullness.memeverse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MyApi {

    private static final String URL = "https://meme-api.herokuapp.com/gimme/";

    public static ApiService apiService = null;

    public static ApiService getApiService(){

        if (apiService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;

    }

    public interface ApiService {
        @GET("DankMemes")
        Call<MemeResponse> getDankMemes();

        @GET("IndianDankMemes")
        Call<MemeResponse> getIndianDankMemes();

        @GET("IndianMeyMeys")
        Call<MemeResponse> getIndianMeyMeys();

        @GET("memes")
        Call<MemeResponse> getMeme();
    }

}
