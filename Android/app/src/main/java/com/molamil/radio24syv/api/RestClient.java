package com.molamil.radio24syv.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.Locale;

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
                .setDateFormat(getDateFormat())
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

    public static String getLocalDate(String dateWithTimeZone) {
        LocalDateTime date = new DateTime(dateWithTimeZone).toLocalDateTime();
        return String.format(Locale.US, "%d/%d", date.getDayOfMonth(), date.getMonthOfYear());
    }

    public static int getIntegerSafely(Integer i, int defaultValue) {
        if (i != null) {
            return i;
        } else {
            return defaultValue; // Horrible null pointer exception if Integer is null
        }
    }

    public static String getDateFormat() {
        return "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'"; // The date format used by the API
    }

    public static String getTextWithoutHtmlTags(String html) {
        html = html.replace("&amp;", "&"); // Ampersand instead of html code
        html = html.replace("<p", "\n<p"); // Line break before <p>
        html = html.replace("<li", "\n<p"); // Line break before <li>
        html = html.trim();
        StringBuilder builder = new StringBuilder();
        int textStart = 0;
        do {
            int textEnd = html.indexOf("<", textStart);
            if (textEnd < 0) {
                textEnd = html.length() - 1;
            }
            if (textEnd > textStart) {
                builder.append(html.substring(textStart, textEnd)); // Second parameter is the end index, NOT the number of characters to copy
            }
            textStart = html.indexOf(">", textEnd) + 1;
        } while ((textStart > 0) && (textStart < html.length()));
        return builder.toString();
    }

    // "Official" way of getting slug from URL: Man kan sådan set også godt slå programmer op på deres slug,
    // så i princippet kan du tage url’en og strippe http://www.radio24syv.dk/programmer/ og den efterfølgende /.
    public static String getProgramSlugFromUrl(String url) {
        if (url == null) {
            return null;
        }

        final String SLASH = "/";
        final String PROGRAMMER = "programmer" + SLASH;

        int startIndex = url.indexOf(PROGRAMMER);
        if (startIndex == -1) {
            return url; // Return the whole URL, something is wrong
        } else {
            startIndex = startIndex + PROGRAMMER.length(); // Start after "programmer/"
        }

        int endIndex = url.indexOf("/", startIndex);
        if (endIndex == -1) {
            endIndex = url.length() - 1;
        } else {
            // Ends before "/" because substring() minuses one (yay..)
        }

        if ((startIndex < url.length()) && (endIndex > startIndex)) {
            return url.substring(startIndex, endIndex);
        } else {
            return url; // Return the whole URL, something is wrong
        }
    }
}