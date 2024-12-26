package com.mobile.movie_project_one.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.search.SearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.mobile.movie_project_one.Adapters.FilmListAdapter;
import com.mobile.movie_project_one.Domains.Film;
import com.mobile.movie_project_one.R;
import com.mobile.movie_project_one.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseDatabase database;
    private Handler sliderHandler = new Handler();
    private FirebaseAuth auth;
    private FirebaseUser user;
    ChipNavigationBar chipNavigationBar;
    AutoCompleteTextView searchview;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        chipNavigationBar = findViewById(R.id.menu);
        searchview = findViewById(R.id.txtSearch);

        // Redirect to LoginActivity if user is not authenticated
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        database = FirebaseDatabase.getInstance();

        // Set default selected item
        chipNavigationBar.setItemSelected(R.id.Home, true);

        initLatest();
        initPopular();
        initTrending();

        // Handle item click events
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.Home) {
                    // Stay in MainActivity or go to the ExplorerActivity
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                } else if (id == R.id.favorites) {
                    // Navigate to FavoriteActivity
                    startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                } else if (id == R.id.logout) {
                    showLogoutDialog();
                }
            }
        });

//navigating to search option
        searchview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });



    }




    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performLogout(); // Call the method to log the user out
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Navigate back to MainActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish(); // Finish this activity
            }
        });
        builder.show();
    }

    private void performLogout() {
        // Sign out from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Clear saved user data (if you're using shared preferences, clear them here)
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Clear all user data
        editor.apply(); // Apply changes

        // Redirect to Sign In activity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
        startActivity(intent);
        finish(); // Finish the current activity
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // If user is not signed in, redirect to SignInActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finish this activity
        }
    }

    private void initPopular() {
        DatabaseReference myRef = database.getReference("Popular");
        binding.progressBarTop.setVisibility(View.VISIBLE);
        ArrayList<Film> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(Film.class));
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewTopMovies.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewTopMovies.setAdapter(new FilmListAdapter(items));
                    }
                    binding.progressBarTop.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors here
            }
        });
    }

    private void initTrending() {
        DatabaseReference myRef = database.getReference("Trending");
        binding.progressBarUpcoming.setVisibility(View.VISIBLE);
        ArrayList<Film> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(Film.class));
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewUpcoming.setAdapter(new FilmListAdapter(items));
                    }
                    binding.progressBarUpcoming.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors here
            }
        });
    }


    private void initLatest() {
        DatabaseReference myRef = database.getReference("Latest");
        binding.progressBarLatest.setVisibility(View.VISIBLE);
        ArrayList<Film> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(Film.class));
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewLatestMovies.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewLatestMovies.setAdapter(new FilmListAdapter(items));
                    }
                    binding.progressBarLatest.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors here
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        chipNavigationBar.setItemSelected(R.id.Home, true); // Set the Home item as selected
    }




}
