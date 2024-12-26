package com.mobile.movie_project_one.Activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.movie_project_one.Adapters.SearchAdapter;
import com.mobile.movie_project_one.Domains.Film;
import com.mobile.movie_project_one.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    DatabaseReference PopularRef, LatestRef, TrendingRef;
    RecyclerView recyclerView;
    ArrayList<Film> movieList;
    ArrayList<Film> originalMovieList;
    SearchAdapter movieAdapter;
    SearchView searchView;
    TextView noResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        PopularRef = FirebaseDatabase.getInstance().getReference("Popular");
        LatestRef = FirebaseDatabase.getInstance().getReference("Latest");
        TrendingRef = FirebaseDatabase.getInstance().getReference("Trending");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noResultsTextView = findViewById(R.id.noResultsTextView);
        noResultsTextView.setVisibility(View.GONE);

        movieList = new ArrayList<>();
        originalMovieList = new ArrayList<>();
        movieAdapter = new SearchAdapter(movieList);
        recyclerView.setAdapter(movieAdapter);
        searchView = findViewById(R.id.searchView);

        // Change text color
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK);
            searchEditText.setHintTextColor(Color.LTGRAY);
        }

        // Set search icon color
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) {
            searchIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }

        // Set close icon color
        ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeIcon != null) {
            closeIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }

        fetchAllMovies();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMovies(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    movieList.clear();
                    movieAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.GONE);
                    noResultsTextView.setVisibility(View.GONE);
                } else {
                    searchMovies(newText);
                }
                return true;
            }
        });
    }

    private void fetchAllMovies() {
        fetchMoviesFromReference(PopularRef);
        fetchMoviesFromReference(LatestRef);
        fetchMoviesFromReference(TrendingRef);
    }

    private void fetchMoviesFromReference(DatabaseReference ref) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Film movie = snapshot.getValue(Film.class);
                    if (movie != null) {
                        originalMovieList.add(movie);
                    }
                }
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FetchMovies", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    private void searchMovies(String query) {
        final String searchQuery = query.toLowerCase().trim();
        ArrayList<Film> filteredList = new ArrayList<>();
        String[] queryWords = searchQuery.split("\\s+");

        for (Film movie : originalMovieList) {
            String title = movie.getTitle().toLowerCase();
            boolean matches = true;

            for (String word : queryWords) {
                if (!title.contains(word)) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                filteredList.add(movie);
            }
        }

        movieList.clear();
        movieList.addAll(filteredList);
        movieAdapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noResultsTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noResultsTextView.setVisibility(View.GONE);
        }
    }
}
