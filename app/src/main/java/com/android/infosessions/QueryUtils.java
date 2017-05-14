package com.android.infosessions;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.infosessions.data.ContactContract;
import com.android.infosessions.data.ContactDbHelper;
import com.android.infosessions.data.SessionContract.SessionEntry;
import com.android.infosessions.data.SessionDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;


public final class QueryUtils {
    public static final String LOG_TAG = MainActivity.class.getName();
    private static ArrayList<String> logos = new ArrayList<>();

    public static Calendar c;
    public static int cMonth;
    public static int cDay;
    public static int cYear;

    private QueryUtils() {
    }


    public static ArrayList<Session> fetchInfos(String requestUrl) {
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
        ArrayList<Session> sessions = extractInfos(jsonResponse);

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
            urlConnection.connect();if (urlConnection.getResponseCode() == 200) {
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
    public static ArrayList<Session> extractInfos(String infosJSON) {

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

                String name = sessionJSONObject.getString("employer");
                if(!name.equals("Closed Info Session") && !name.equals("Closed Information Session") ) {
                    Session session = new Session(sessionJSONObject, getEmployerLogo(name));
                    sessions.add(session);
                    //insertSession(sessionJSONObject, context);
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

    private static void insertSession(JSONObject session, Context context) throws JSONException{
        String mEmployer = session.getString("employer");
        String mStartTime = session.getString("start_time");
        String mEndTime = session.getString("end_time");
        String mDate = session.getString("date");
        String mDay = session.getString("day");
        String mWebsite = session.getString("website");
        String mLink = session.getString("link");
        String mDescription = session.getString("description");
        String mLogo = getEmployerLogo(mEmployer);
        if(mDescription.isEmpty()) {
            mDescription = "Employer's Description is not provided.";
        }
        JSONObject building = session.getJSONObject("building");
        String mCode = building.getString("code");
        String mBuildingName = building.getString("name");
        String mRoom = building.getString("room");
        String mMapUrl = building.getString("map_url");

        ContactDbHelper mDbHelper = new ContactDbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SessionEntry.COLUMN_SESSION_EMPLOYER, mEmployer);
        values.put(SessionEntry.COLUMN_SESSION_START_TIME, mStartTime);
        values.put(SessionEntry.COLUMN_SESSION_END_TIME, mEndTime);
        values.put(SessionEntry.COLUMN_SESSION_DATE, mDate);
        values.put(SessionEntry.COLUMN_SESSION_DAY, mDay);
        values.put(SessionEntry.COLUMN_SESSION_WEBSITE, mWebsite);
        values.put(SessionEntry.COLUMN_SESSION_LINK, mLink);
        values.put(SessionEntry.COLUMN_SESSION_DESCRIPTION, mDescription);
        values.put(SessionEntry.COLUMN_SESSION_BUILDING_CODE, mCode);
        values.put(SessionEntry.COLUMN_SESSION_BUILDING_NAME, mBuildingName);
        values.put(SessionEntry.COLUMN_SESSION_BUILDING_ROOM, mRoom);
        values.put(SessionEntry.COLUMN_SESSION_MAP_URL, mMapUrl);


        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(SessionEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(context, "Error with saving session", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(context, "session saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
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
        logos.add("yext");
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











