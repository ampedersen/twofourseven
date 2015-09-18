package com.molamil.radio24syv.api;

import com.molamil.radio24syv.api.model.Broadcast;
import com.molamil.radio24syv.api.model.Host;
import com.molamil.radio24syv.api.model.MetaInfo;
import com.molamil.radio24syv.api.model.Page;
import com.molamil.radio24syv.api.model.Podcast;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by jens on 18/09/15.
 */
public interface RestApi {

    // Broadcasts

    @GET("/broadcasts/latest/{limit}/{offset}")
    Call<List<Broadcast>> getLatestBroadcasts(@Path("limit") Integer limit, @Path("offset") Integer offsetHours);

    @GET("/broadcasts/next/{limit}/{offset}")
    Call<List<Broadcast>> getNextBroadcasts(@Path("limit") Integer limit, @Path("offset") Integer offsetHours);

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

    @GET("/podcasts/{id}")
    Call<Podcast> getPodcast(@Path("id") Integer id);

//    @GET("/podcasts/metadata/{start}")
//    Call<MetaInfo> getPodcastMetaInfo(@Path("start") String timestamp);
//
//    @GET("/podcasts/permalink/{id}")
//    Call<MetaInfo> getPodcastPermalink(@Path("id") int id);

    // Programs

    // Searches

    // Stories (empty?)
}