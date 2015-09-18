package com.molamil.radio24syv.api;

import com.molamil.radio24syv.api.model.Broadcast;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by jens on 18/09/15.
 */
public interface RadioApi {

//    // "/api/42/getDummieContent?test=test"
//    @GET("/api/{id}/getDummieContent")
//    public DummieContent getDummieContent(@Path("id") Integer id, @Query("test") String strTest);

//    @GET("/users/{user}/repos")
//    Call<List<Repo>> listRepos(@Path("user") String user);

//    @GET("/broadcasts/next/4/0")
//    public Call<List<Broadcast>> getBroadcasts();

    @GET("/broadcasts/next/4/0")
    Call<List<Broadcast>> getBroadcasts();


}