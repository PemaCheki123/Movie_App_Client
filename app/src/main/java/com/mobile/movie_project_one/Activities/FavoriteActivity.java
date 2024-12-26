package com.mobile.movie_project_one.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.mobile.movie_project_one.Adapters.favAdapter;
import com.mobile.movie_project_one.Domains.Film;
import com.mobile.movie_project_one.R;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Film> favoriteMoviesList; // ArrayList to hold Film objects
    favAdapter adapter; // Adapter for displaying favorite movies
    DatabaseReference favRef;
    ImageView backButton;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        String userID = mAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.listfav);
        backButton = findViewById(R.id.back_button);
        favoriteMoviesList = new ArrayList<>();
        chipNavigationBar = findViewById(R.id.menu);

        // Set up RecyclerView with a LinearLayoutManager and adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new favAdapter(favoriteMoviesList, this);
        recyclerView.setAdapter(adapter);

        // Set favorites item as selected in the navigation bar
        chipNavigationBar.setItemSelected(R.id.favorites, true);

        // Handle item click events
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.favorites) {
                    // Do nothing since already in FavoriteActivity
                } else if (id == R.id.Home) {
                    startActivity(new Intent(FavoriteActivity.this, MainActivity.class)); // Assuming MainActivity is your home activity
                    finish(); // Finish this activity to prevent going back to it
                } else if (id == R.id.logout) {
                    showLogoutDialog();
                }
            }
        });

        // Reference to Firebase 'favorites' node
        favRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Favorites");

        backButton.setOnClickListener(v -> finish()); // Finishes the activity and goes back

        // Fetch favorite movies from Firebase
        fetchFavoriteMovies();
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

    private void fetchFavoriteMovies() {
        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteMoviesList.clear(); // Clear the old list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Film movie = snapshot.getValue(Film.class); // Assuming Film class is properly modeled
                    if (movie != null) {
                        favoriteMoviesList.add(movie);
                        Log.d("FavoriteActivity", "Movie added: " + movie.getTitle()); // Log movie title
                    } else {
                        Log.e("FavoriteActivity", "Failed to get movie data");
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter about data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FavoriteActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
