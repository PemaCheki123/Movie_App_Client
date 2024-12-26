package com.mobile.movie_project_one.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobile.movie_project_one.R;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editReEnterPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    TextView signIn;
    ImageView buttonback;




    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextEmail = findViewById(R.id.EmailAddress);
        editTextPassword = findViewById(R.id.Password);
        editReEnterPassword =findViewById(R.id.ReEnterPassword);
        buttonReg = findViewById(R.id.RegButton);
        mAuth = FirebaseAuth.getInstance();
        signIn = findViewById(R.id.loginButton);
        buttonback = findViewById(R.id.buttonback);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backintent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(backintent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String email, password,re_password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                re_password = String.valueOf(editReEnterPassword.getText());

                //if it is empty
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(re_password)){
                    Toast.makeText(RegisterActivity.this, "Re-Enter the Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.equals(re_password)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(RegisterActivity.this, "Successfully Account Created", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.putExtra("fromRegistration", true);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(RegisterActivity.this,"Pass do not match.", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}