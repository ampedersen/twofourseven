package com.molamil.radio24syv.api;

import com.molamil.radio24syv.api.model.Broadcast;
import com.molamil.radio24syv.api.model.ConciseProgram;
import com.molamil.radio24syv.api.model.Host;
import com.molamil.radio24syv.api.model.MetaInfo;
import com.molamil.radio24syv.api.model.Page;
import com.molamil.radio24syv.api.model.Podcast;
import com.molamil.radio24syv.api.model.Program;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by jens on 18/09/15.
 */
public interface RestApi {

    // Broadcasts

    @GET("/broadcasts/latest/{limit}/{offset}")
    Call<List<Broadcast>> getLatestBroadcasts(@Path("limit") Integer amount, @Path("offset") Integer offsetHours);

    @GET("/broadcasts/next/{limit}/{offset}")
    Call<List<Broadcast>> getNextBroadcasts(@Path("limit") Integer amount, @Path("offset") Integer offsetHours);

    // Hosts

    @GET("/hosts")
    Call<List<Host>> getHosts();

    @GET("/hosts/{slug}")
    Call<Host> getHost(@Path("slug") String slug);

    // Pages

    @GET("/pages/frontpage")
    Call<Page> getFrontPage();

    @GET("/pages/{slug}")
    Call<Page> getPage(@Path("slug") String slug);

    // Podcasts

//    @GET("/podcasts/program/{id}?size={size}&p={p}&year={year}&month={month}")
//    Call<List<Podcast>> getPodcasts(@Path("id") String programId, @Path("size") Integer amount, @Path("p") Integer pageOffset, @Path("year") Integer year, @Path("month") Integer month);

//    @GET("/podcasts/program/{id}")
//    Call<List<Podcast>> getPodcasts(@Path("id") String programId);

//    @GET("/podcasts/program/{id}")
//    Call<List<Podcast>> getPodcasts(@Path("id") String programId, @Query("size") Integer amount, @Query("p") Integer pageOffset);

    @GET("/podcasts/program/{id}")
    Call<List<Podcast>> getPodcasts(@Path("id") Integer programId, @Query("size") Integer amount, @Query("p") Integer pageOffset);

    // Programs

    @GET("programs")
    Call<List<ConciseProgram>> getPrograms();

    @GET("programs/topic/{slug}")
    Call<List<ConciseProgram>> getPrograms(@Path("slug") String topicSlug);

    @GET("programs/topic/{slug}")
    Call<List<ConciseProgram>> getPrograms(@Path("slug") Integer topicId);

    @GET("programs/{slug}")
    Call<List<Program>> getProgram(@Path("slug") String programSlug);

    @GET("programs/{slug}")
    Call<List<Program>> getProgram(@Path("slug") Integer programId);

    // Searches

    // Stories (empty?)
}