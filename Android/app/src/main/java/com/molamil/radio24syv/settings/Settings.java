package com.molamil.radio24syv.settings;

import android.content.Context;
import android.util.Log;

import com.molamil.radio24syv.RadioLibrary;
import com.molamil.radio24syv.settings.model.PodcastInfo;
import com.molamil.radio24syv.settings.model.ProgramInfo;

import java.util.List;

/**
 * Created by jens on 24/09/15.
 */
public class Settings {
    public final static long DOWNLOAD_ID_UNKNOWN = -1;
    public final static int PODCAST_ID_UNKNOWN = -1;

    private SettingsDatabase settings;

    private static Settings instance = null;

    public static void initialize(Context context) {
        instance = new Settings(context);
    }

    public static Settings get() {
        if (instance == null) {
            // Not good
            Log.e("JJJ", "Settings must be initialized before you can use it");
        }
        return instance;
    }

    public Settings(Context context) {
        settings = new SettingsDatabase(context);
    }

    public void addLibraryIds(int podcastId, long downloadId) {
        settings.addLibraryIds(podcastId, downloadId);
    }

    public long getLibraryDownloadId(int podcastId) {
        return settings.getLibraryDownloadId(podcastId);
    }

    public int getLibraryPodcastId(long downloadId) {
        return settings.getLibraryPodcastId(downloadId);
    }

    public void removeLibraryIds(int podcastId) {
        settings.removeLibraryIds(podcastId);
    }

    public void addProgram(ProgramInfo program) {
        settings.addProgram(program);
    }

    public ProgramInfo getProgram(int programId) {
        return settings.getProgram(programId);
    }

    public List<ProgramInfo> getPrograms() {
        return settings.getPrograms();
    }

    public void removeProgram(int programId) {
        settings.removeProgram(programId);
    }

    public void addPodcast(PodcastInfo podcast) {
        settings.addPodcast(podcast);
    }

    public PodcastInfo getPodcast(int podcastId) {
        return settings.getPodcast(podcastId);
    }

    public List<PodcastInfo> getPodcasts(int programId) {
        return settings.getPodcasts(programId);
    }

    public int getPodcastCount(int programId) {
        return settings.getPodcastCount(programId);
    }

    public void removePodcast(int podcastId) {
        settings.removePodcast(podcastId);
    }

    public void deleteAll(Context context) {
        for (PodcastInfo podcast : settings.getPodcasts()) {
            Log.d("JJJ", "Deleting " + podcast.getPodcastId() + " " + podcast.getTitle());
            RadioLibrary.getInstance().remove(context, podcast);
        }
        Log.d("JJJ", "Deleting database");
        settings.dropTables();
        settings.createTables();
    }
}
