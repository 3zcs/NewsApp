package me.a3zcs.booklisting.newsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 23/07/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{
    List<News> newsList = new ArrayList<>();
    Context context;
    public NewsAdapter(Context context,List<News> newsList) {
        this.newsList = newsList;
        this.context = context;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item,parent,false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        holder.title.setText(newsList.get(position).getTitle());
        holder.section.setText(newsList.get(position).getSectionName());
        holder.date.setText(newsList.get(position).getDate());
        holder.body.setText(newsList.get(position).getBody());

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder{
        TextView title,section,date,body;
        public NewsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            section = itemView.findViewById(R.id.section);
            date = itemView.findViewById(R.id.date);
            body = itemView.findViewById(R.id.body);
        }
    }
}
