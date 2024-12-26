package com.mobile.movie_project_one.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.mobile.movie_project_one.Activities.DetailActivity;
import com.mobile.movie_project_one.Domains.Film;
import com.mobile.movie_project_one.R;

import java.util.ArrayList;

public class favAdapter extends RecyclerView.Adapter<favAdapter.FavHolder> {

    private Context context; // Added context as a member variable
    private ArrayList<Film> items;
    // ArrayList to hold Film objects

    public favAdapter(ArrayList<Film> items, Context context) {
        this.items = items;
        this.context = context; // Initialize context in the constructor
    }

    @NonNull
    @Override
    public FavHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_view, parent, false);
        return new FavHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavHolder holder, int position) {
        // Bind data to the views
        Film currentFilm = items.get(position);
        holder.titleView.setText(currentFilm.getTitle());
        holder.hourView.setText(currentFilm.getTime());


        // Load the poster image using Glide
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CenterCrop(), new RoundedCorners(30));

        Glide.with(context)
                .load(currentFilm.getPoster())
                .apply(requestOptions)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", currentFilm); // Pass the movie object to DetailActivity
            context.startActivity(intent); // Start DetailActivity
        });

    }

    public class FavHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView hourView;

        public FavHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            imageView = itemView.findViewById(R.id.image_poster);
            titleView = itemView.findViewById(R.id.title);
            hourView = itemView.findViewById(R.id.time);
        }
    }

    @Override
    public int getItemCount() {
        return items.size(); // Return the number of items
    }
}
