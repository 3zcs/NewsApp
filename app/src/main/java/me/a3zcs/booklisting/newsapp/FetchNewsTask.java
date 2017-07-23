package me.a3zcs.booklisting.newsapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 23/07/17.
 */

public class FetchNewsTask extends AsyncTaskLoader<List<News>> {
    private final String KEY = "api-key=42ceb490-6baa-45c5-bbf4-6c456238f6c6";
    private final String ENDPOINT = "https://content.guardianapis.com/search?format=json&show-fields=bodyText&";

    public FetchNewsTask(Context context) {
        super(context);
        onContentChanged();
    }

    @Override
    public List<News> loadInBackground() {
        List<News> newsList = new ArrayList<>();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ENDPOINT + KEY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.connect();
            InputStream stream = new BufferedInputStream(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
            StringBuilder builder = new StringBuilder();

            String inputString;
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString);
            }
            Log.i("result", builder.toString());

            JSONObject topLevel = new JSONObject(builder.toString());
            if (topLevel.has("response")) {
                JSONObject object = topLevel.getJSONObject("response");
                JSONArray array = object.getJSONArray("results");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject oneNews = array.getJSONObject(i);
                    newsList.add(new News(oneNews.getString("sectionName"),
                            oneNews.getString("webTitle"),
                            oneNews.getString("webPublicationDate"),
                            oneNews.getJSONObject("fields").getString("bodyText")));
                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection == null)
                connection.disconnect();
            return newsList;
        }
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged())
            forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
