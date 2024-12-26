package com.mobile.movie_project_one.Adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.movie_project_one.Activities.DetailActivity;
import com.mobile.movie_project_one.Domains.Film;
import com.mobile.movie_project_one.R;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MovieViewHolder> {
    private ArrayList<Film> movieList;
    private Context context;

    public SearchAdapter(ArrayList<Film> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view, parent, false);
        context = parent.getContext();
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Film movie = movieList.get(position);
        holder.Title.setText(movie.getTitle());
        // Bind other movie details here
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", movie); // Pass the movie object to DetailActivity
            context.startActivity(intent); // Start DetailActivity
        });

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView Title;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.title);
        }
    }
}
