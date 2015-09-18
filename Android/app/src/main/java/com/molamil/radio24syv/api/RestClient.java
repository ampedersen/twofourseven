package com.molamil.radio24syv.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by jens on 18/09/15.
 */
public class RestClient
{
    private static final String BASE_URL = "http://api.radio24syv.dk";
    private RadioApi api;

    public RestClient()
    {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setLogLevel(RestAdapter.LogLevel.FULL)
//                .setEndpoint(BASE_URL)
//                .setConverter(new GsonConverter(gson))
//                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

//        api = restAdapter.create(RadioApi.class);
        api = retrofit.create(RadioApi.class);
    }

    public RadioApi getApi()
    {
        return api;
    }
}