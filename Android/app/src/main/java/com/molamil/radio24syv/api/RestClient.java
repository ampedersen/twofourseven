package com.molamil.radio24syv.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Uses Retrofit library to call Radio24syv API.
 *
 * The model classes have been generated like this:
 * 1) Execute each API call (e.g. http://api.radio24syv.dk/Broadcasts.html)
 *    in a browser and copy the JSON response clipboard.
 * 2) Open http://www.jsonschema2pojo.org (works with Chrome)
 *    - Paste into the box.
 *    - Select: Source type "JSON"
 *    - Select: Annotation style "Gson"
 *    - Select: Use double numbers
 *    - Click Jar button to download .jar file
 * 3) In Finder, right-click .jar file and Open With - Archive Utility
 *    to unpack.
 * 4) Copy each unpacked .java file into the Android Studio project.
 *
 * Created by jens on 18/09/15.
 */
public class RestClient
{
    private RestApi api;

    private RestClient(String baseUrl)
    {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(RestApi.class);
    }

    private static RestClient instance = null;

    public static void initialize(String baseUrl) {
        instance = new RestClient(baseUrl);
    }

    public static RestApi getApi() {
        if (instance == null) {
            Log.e("JJJ", "Unable to getApi() because RestClient instance is null - you must call RestClient.initialize(baseUrl) first");
            return null; // Return, this is not good
        }
        return instance.api;
    }

    // The local o'clock converted from a date with time zone. Uses Joda-time library to ensure consistent time zone conversion across Android versions.
    public static String getLocalTime(String dateWithTimeZone) {
        LocalDateTime time = new DateTime(dateWithTimeZone).toLocalDateTime();
        return time.toString("HH.mm");
    }

    public static int getIntegerSafely(Integer i, int defaultValue) {
        if (i != null) {
            return i;
        } else {
            return defaultValue; // Horrible null pointer exception if Integer is null
        }
    }
}