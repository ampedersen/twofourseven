package com.molamil.radio24syv;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.molamil.radio24syv.api.RestClient;
import com.molamil.radio24syv.api.model.ConciseProgram;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.StorageDatabase;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;

/**
 * Created by jens on 28/09/15.
 */
public class ProgramLibrary {

//    private static ProgramLibrary instance = null;
//
//    public static void initialize(Context context) {
//        instance = new ProgramLibrary(context);
//    }
//
//    public static ProgramLibrary get() {
//        if (instance == null) {
//            // Not good
//            Log.e("JJJ", "ProgramLibrary must be initialized before you can use it");
//        }
//        return instance;
//    }
//
//    public ProgramLibrary(Context context) {
//        // Not using context for anything at the moment...
//
//        RestClient.getApi().getPrograms().enqueue(new Callback<List<ConciseProgram>>() {
//            @Override
//            public void onResponse(Response<List<ConciseProgram>> response) {
//
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//            }
//        });
//    }
//
//    public List<ProgramInfo> getPrograms() {
//    }
//
//    public List<TopicInfo> getTopics() {
//        // Return list instantly from local storage (if available)
//        // Download updated list, save to local storage
//    }
//
//    public interface ProgramLibraryListener {
//        void OnProgramsUpdated(List<ProgramInfo> programs);
//        void OnTopicsUpdated(List<ProgramInfo> programs);
//    }
}
