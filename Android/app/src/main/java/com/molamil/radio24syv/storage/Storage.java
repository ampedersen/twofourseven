package com.molamil.radio24syv.storage;

import android.content.Context;
import android.util.Log;

import com.molamil.radio24syv.storage.model.PodcastInfo;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jens on 24/09/15.
 */
public class Storage {
    public final static long DOWNLOAD_ID_UNKNOWN = -1;
    public final static int PODCAST_ID_UNKNOWN = -1;
    public final static String COLOR_UNKNOWN = "";
    public final static String TOPIC_ID_UNKNOWN = "";
    public final static int PROGRAM_ID_UNKNOWN = -1;

    private StorageDatabase database;
    private HashMap<String, TopicInfo> cachedTopicById = new HashMap<>();
    private ArrayList<TopicInfo> cachedTopicsSorted = new ArrayList<>();

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
        database.writeProgramInfo(program);
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

//    public void addTopic(String topic) {
//        database.addTopic(topic);
//    }

//    public void setTopicColor(String topic, String color) {
//        database.setTopicColor(topic, color);
//    }

//    public String getTopicColor(String topic) {
//        return database.getTopicColor(topic);
//    }

//    public List<String> getTopicNames() {
//        return database.getTopicNames();
//    }

    public void addTopics(List<TopicInfo> topics) {
        clearCache(); // Clear cache to make it initialize next time it is accessed
        database.addTopics(topics);
    }

    public TopicInfo getTopic(String topicId) {
        initializeCacheIfNeeded();
        return cachedTopicById.get(topicId);
    }

    public Collection<TopicInfo> getTopics() {
        initializeCacheIfNeeded();
        return cachedTopicsSorted;
    }

    public void addPlayerHistory(int programId, String date) {
        database.addPlayerHistory(programId, date);
    }

    public List<ProgramInfo> getPlayerHistory(int limit) {
        return database.getPlayerHistory(limit);
    }

    public void addRelatedPrograms(int programId, List<Integer> relatedProgramIds) {
        database.addRelatedPrograms(programId, relatedProgramIds);
    }

    public List<ProgramInfo> getRelatedPrograms(int programId, int limit) {
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

    public void deleteHistory(Context context) {
        database.deletePlayerHistory();
    }

    private void clearCache() {
        cachedTopicById.clear();
        cachedTopicsSorted.clear();
    }

    private void initializeCacheIfNeeded() {
        boolean isInitialized = (cachedTopicById.size() > 0);
        if (isInitialized) {
            return;
        }

        for (TopicInfo t : database.getTopics()) {
            cachedTopicById.put(t.getTopicId(), t);
        }

        cachedTopicsSorted.addAll(cachedTopicById.values());
        Collections.sort(cachedTopicsSorted, new Comparator<TopicInfo>() {
            @Override
            public int compare(TopicInfo lhs, TopicInfo rhs) {
                return lhs.getTopicText().compareToIgnoreCase(rhs.getTopicText());
            }
        });
    }

}
