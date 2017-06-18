package com.android.infosessions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.infosessions.data.DbHelper;
import com.android.infosessions.data.FilterContract.FilterEntry;
import com.android.infosessions.data.SessionContract.SessionEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public final class QueryUtils extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getName();
    private static ArrayList<String> logos = new ArrayList<>();
    public static final String LOGO_URL = "https://logo.clearbit.com/";
    public static final String DOMAIN = ".com";

    public static Calendar c;

    private QueryUtils() {
    }


    public static ArrayList<Session> fetchInfos(String requestUrl, Context context) {

        DbHelper mDbHelper = new DbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(SessionEntry.TABLE_NAME, null, null);
        db.delete(FilterEntry.TABLE_NAME, null, null);

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<Session> sessions = extractInfos(jsonResponse, context);

        // Return the {@link Event}
        return sessions;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch(MalformedURLException e) {
            Log.e(LOG_TAG, "Error with string url", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Session} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Session> extractInfos(String infosJSON, Context context) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Session> sessions = new ArrayList<>();


        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject root = new JSONObject(infosJSON);
            JSONArray sessionsJSONArray = root.getJSONArray("data");
            for(int i = 0; i < sessionsJSONArray.length(); i++) {
                JSONObject sessionJSONObject = sessionsJSONArray.getJSONObject(i);

                String employer = sessionJSONObject.getString("employer");
                
                if(!employer.equals("Closed Info Session") && !employer.equals("Closed Information Session") ) {
                    //employer = "google";
                    if(employer.contains("**")) {
                        employer = employer.substring(employer.indexOf("**", employer.indexOf("**") + 1), employer.length());
                    }
                    if(employer.contains("*")) {
                        employer = employer.substring(employer.indexOf("*", employer.indexOf("*") ), employer.length());
                    }
                    employer = employer.replace(" ", "");
                    employer = employer.replace("Inc.", "");
                    employer = employer.replace(".", "");
                    employer = employer.trim();

                    URL logo_url = createUrl(LOGO_URL+ employer + DOMAIN);
                    Bitmap bmp = null;
                    try {
                        bmp = BitmapFactory.decodeStream(logo_url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(bmp == null) {
                        bmp = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.nonlogo);
                    }

                    Session session = new Session(sessionJSONObject, bmp);
                    sessions.add(session);
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        // Return the list of earthquakes
        return sessions;
    }

    public static String getEmployerLogo(String str) {
        logos.add("a500px");
        logos.add("a9");
        logos.add("adroll");
        logos.add("amazon");
        logos.add("arista");
        logos.add("autodesk");
        logos.add("bazaarvoice");
        logos.add("bloomberg");
        logos.add("cibc");
        logos.add("dac_group");
        logos.add("digiflare");
        logos.add("electronic_arts");
        logos.add("facebook");
        logos.add("genesys");
        logos.add("google");
        logos.add("groupby");
        logos.add("hootsuite");
        logos.add("league_inc");
        logos.add("loblaw_digital");
        logos.add("meraki");
        logos.add("microsoft");
        logos.add("pointclickcare");
        logos.add("rbc_technology");
        logos.add("redfin");
        logos.add("td_technology");
        logos.add("tribalscale");
        logos.add("twitter");
        logos.add("uber");
        logos.add("uken_games");
        logos.add("watpad");
        logos.add("wave_accounting");
        logos.add("whatsapp");
        logos.add("yelp");
        logos.add("ic_domain_black_24dp");
        String name = str;
        name = name.replace(" ", "_");
        name = name.toLowerCase();
        boolean replaced = false;
        for(int i = 0; i < logos.size(); i++) {
            if(name.compareToIgnoreCase(logos.get(i)) == 0 || name.contains(logos.get(i)) || logos.get(i).contains(name)) {
                name = logos.get(i);
                replaced = true;
                break;
            }
        }
        if(!replaced) {
            name = "nologo";
        }
        return name;
    }

}











