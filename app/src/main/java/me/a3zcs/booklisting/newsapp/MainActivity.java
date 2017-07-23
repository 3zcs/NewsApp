package me.a3zcs.booklisting.newsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{


    List<News>newses = new ArrayList<>();
    RecyclerView recyclerView;
    TextView noResult;
    LinearLayoutManager manager;
    NewsAdapter adapter;
    LoaderManager loaderManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.news_list);
        noResult = (TextView) findViewById(R.id.no_result);
        manager = new LinearLayoutManager(this);
        loaderManager = getSupportLoaderManager();
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
        }

        loaderManager.initLoader(1,null,this);

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

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new FetchNewsTask(this);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        handelList(data);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

    }

}
