package com.molamil.radio24syv.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.molamil.radio24syv.RadioLibrary;
import com.molamil.radio24syv.storage.model.PodcastInfo;
import com.molamil.radio24syv.storage.model.ProgramInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jens on 24/09/15.
 */
public class Storage {
    public final static long DOWNLOAD_ID_UNKNOWN = -1;
    public final static int PODCAST_ID_UNKNOWN = -1;
    public final static String COLOR_UNKNOWN = "";

    private StorageDatabase database;

    private static Storage instance = null;

    public static void initialize(Context context) {
        instance = new Storage(context);
    }

    public static Storage get() {
        if (instance == null) {
            // Not good
            Log.e("JJJ", "Storage must be initialized before you can use it");
        }
        return instance;
    }

    public Storage(Context context) {
        database = new StorageDatabase(context);
    }

    public void addLibraryIds(int podcastId, long downloadId) {
        database.addLibraryIds(podcastId, downloadId);
    }

    public long getLibraryDownloadId(int podcastId) {
        return database.getLibraryDownloadId(podcastId);
    }

    public int getLibraryPodcastId(long downloadId) {
        return database.getLibraryPodcastId(downloadId);
    }

    public void removeLibraryIds(int podcastId) {
        database.removeLibraryIds(podcastId);
    }

    public void addProgram(ProgramInfo program) {
        database.addProgram(program);
    }

    public void addPrograms(List<ProgramInfo> programs) {
        database.addPrograms(programs);
    }

    public ProgramInfo getProgram(int programId) {
        return database.getProgram(programId);
    }

    public List<ProgramInfo> getPrograms() {
        return database.getPrograms();
    }

    public List<ProgramInfo> getProgramsWithPodcastsInLibrary() {
        return database.getProgramsWithPodcastsInLibrary();
    }

    public void removeProgram(int programId) {
        database.removeProgram(programId);
    }

    public void addPodcast(PodcastInfo podcast) {
        database.addPodcast(podcast);
    }

    public PodcastInfo getPodcast(int podcastId) {
        return database.getPodcast(podcastId);
    }

    public List<PodcastInfo> getPodcasts(int programId) {
        return database.getPodcasts(programId);
    }

    public int getPodcastCount(int programId) {
        return database.getPodcastCount(programId);
    }

    public void removePodcast(int podcastId) {
        database.removePodcast(podcastId);
    }

    public void addTopic(String topic) {
        database.addTopic(topic);
    }

    public void setTopicColor(String topic, String color) {
        database.setTopicColor(topic, color);
    }

    public String getTopicColor(String topic) {
        return database.getTopicColor(topic);
    }

    public List<String> getTopicNames() {
        return database.getTopicNames();
    }

    public void addPlayerHistory(int programId, String date) {
        database.addPlayerHistory(programId, date);
    }

    public List<Integer> getPlayerHistory(int limit) {
        return database.getPlayerHistory(limit);
    }

    public void addRelatedPrograms(int programId, List<Integer> relatedProgramIds) {
        database.addRelatedPrograms(programId, relatedProgramIds);
    }

    public List<Integer> getRelatedPrograms(int programId, int limit) {
        return database.getRelatedPrograms(programId, limit);
    }
    
    public void deleteAll(Context context) {
        for (PodcastInfo podcast : database.getPodcasts()) {
            Log.d("JJJ", "Deleting " + podcast.getPodcastId() + " " + podcast.getTitle());
            RadioLibrary.getInstance().remove(context, podcast);
        }
        Log.d("JJJ", "Deleting database");
        database.dropTables();
        database.createTables();
    }
}
