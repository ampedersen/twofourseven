package com.molamil.radio24syv.api;

import com.molamil.radio24syv.api.model.Broadcast;
import com.molamil.radio24syv.api.model.ConciseProgram;
import com.molamil.radio24syv.api.model.Host;
import com.molamil.radio24syv.api.model.Page;
import com.molamil.radio24syv.api.model.Podcast;
import com.molamil.radio24syv.api.model.Program;
import com.molamil.radio24syv.api.model.Search;
import com.molamil.radio24syv.api.model.TopicColors;

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

    // Info

    @GET("info/topics")
    Call<TopicColors> getTopicColors();

    @GET("info/topics/{topic}")
    Call<String> getTopicColor(@Path("topic") String topic);

    // Pages

    @GET("/pages/frontpage")
    Call<Page> getFrontPage();

    @GET("/pages/{slug}")
    Call<Page> getPage(@Path("slug") String slug);

    // Podcasts

    @GET("/podcasts/program/{id}")
    Call<List<Podcast>> getPodcasts(@Path("id") Integer programId, @Query("size") Integer amount, @Query("p") Integer pageOffset);

    @GET("/podcasts/news")
    Call<List<Podcast>> getLatestNewsPodcasts();

    // Programs

    @GET("programs")
    Call<List<ConciseProgram>> getPrograms();

    @GET("programs/topic/{slug}")
    Call<List<ConciseProgram>> getPrograms(@Path("slug") String topicSlug);

    @GET("programs/topic/{slug}")
    Call<List<ConciseProgram>> getPrograms(@Path("slug") Integer topicId);

    @GET("programs/{slug}")
    Call<Program> getProgram(@Path("slug") String programSlug);

    @GET("programs/{slug}")
    Call<Program> getProgram(@Path("slug") Integer programId);

    @GET("programs/popular/yesterday/{limit}")
    Call<List<Program>> getPopularPrograms(@Path("limit") Integer amount);

    // Searches

    @GET("search/{query}")
    Call<Search> search(@Path("query") String query, @Query("type") String type, @Query("limit") Integer amount, @Query("offset") Integer pageOffset);

    // Stories (empty?)

}