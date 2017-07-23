package me.a3zcs.booklisting.newsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
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

public class MainActivity extends AppCompatActivity {


    List<News>newses = new ArrayList<>();
    RecyclerView recyclerView;
    TextView noResult;
    LinearLayoutManager manager;
    NewsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.news_list);
        noResult = (TextView) findViewById(R.id.no_result);
        manager = new LinearLayoutManager(this);
        if (!isNetworkAvailable(MainActivity.this)) {
            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.check_network), Toast.LENGTH_LONG).show();
            noResult.setVisibility(View.VISIBLE);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey("news") && savedInstanceState.containsKey("position")) {
            newses = savedInstanceState.getParcelableArrayList("news");


            Parcelable state = savedInstanceState.getParcelable("position");
            recyclerView.setLayoutManager(manager);
            adapter = new NewsAdapter(this,newses);
            recyclerView.setAdapter(adapter);
            handelList(newses);
            manager.onRestoreInstanceState(state);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new NewsAdapter(this,newses);
            recyclerView.setAdapter(adapter);
            new fetchNewsTask().execute();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("news", (ArrayList<? extends Parcelable>) newses);
        outState.putParcelable("position", manager.onSaveInstanceState());
    }

    public void handelList(List<News> newsList) {
        this.newses = newsList;
        adapter.newsList = newsList;
        adapter.notifyDataSetChanged();
        if (!newsList.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            noResult.setVisibility(View.GONE);
        } else {
            noResult.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private class fetchNewsTask extends AsyncTask<String, Void, List<News>> {
        private final String KEY = "api-key=42ceb490-6baa-45c5-bbf4-6c456238f6c6";
        private final String ENDPOINT = "https://content.guardianapis.com/search?format=json&show-fields=bodyText&";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<News> doInBackground(String... strings) {
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
        protected void onPostExecute(List<News> bookList) {
            handelList(bookList);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
