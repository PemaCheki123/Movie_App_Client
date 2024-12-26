package com.mobile.movie_project_one.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.movie_project_one.Adapters.CategoryEachFilmAdapter;
import com.mobile.movie_project_one.Domains.Film;
import com.mobile.movie_project_one.databinding.ActivityDetailBinding;

import eightbitlab.com.blurview.RenderScriptBlur;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    Button addFavButton;
    FirebaseAuth mAuth;
    String userID;
    Film film;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        addFavButton = binding.favbutton;
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        film = (Film) getIntent().getSerializableExtra("object");
        if (film == null) {
            Log.e("DetailActivity", "Film object is null");
            finish();
            return;
        }

        // Update the title variable here
        String title = film.getTitle();
        isFavorite(userID, title);
        setVariable();

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void setVariable() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new GranularRoundedCorners(0, 0, 50, 50));

        Glide.with(this)
                .load(film.getPoster())
                .apply(requestOptions)
                .into(binding.filmPic);

        binding.titleTxt.setText(film.getTitle());
        binding.movieSummary.setText(film.getDescription());

        binding.watchTrailerBtn.setOnClickListener(v -> {
            String id = film.getTrailer().replace("https://www.youtube.com/watch?v=", "");
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(film.getTrailer()));

            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(webIntent);
            }
        });

        binding.backImg.setOnClickListener(v -> finish());

        binding.share.setOnClickListener(v -> {
            String trailerUrl = film.getTrailer();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this trailer: " + trailerUrl);
            startActivity(Intent.createChooser(shareIntent, "Share trailer via"));
        });

        addFavButton.setOnClickListener(v -> toggleFavorite(film));

        // Other setup code...
    }

    private void toggleFavorite(Film film) {
        if (mAuth.getCurrentUser() != null) {
            String movieID = film.getTitle(); // Use movie title as ID, or use a unique identifier if available
            DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Favorites").child(movieID);

            favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // If movie is already in favorites, remove it
                        favRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("DetailActivity", "Removed " + movieID + " from favorites.");
                                Toast.makeText(DetailActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                addFavButton.setText("Add to Favorites");
                            }
                        });
                    } else {
                        // If movie is not in favorites, add it
                        favRef.setValue(film).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("DetailActivity", "Added " + movieID + " to favorites.");
                                Toast.makeText(DetailActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                                addFavButton.setText("Remove from Favorites");
                            } else {
                                Log.e("DetailActivity", "Failed to add " + movieID + " to favorites.");
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(DetailActivity.this, "Failed to update favorites", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(DetailActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
        }
    }

    private void isFavorite(String userID, String title) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userID)
                .child("Favorites")
                .child(title); // Use the correct path

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    addFavButton.setText("Remove from Favorites");
                } else {
                    addFavButton.setText("Add to Favorites");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DetailActivity", "Failed to check if movie is favorite: " + error.getMessage());
            }
        });
    }
}
