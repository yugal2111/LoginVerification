package com.yug.loginverification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class GoogleActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    Button logout;
    TextView name, email;
    ProgressDialog progressDialog;
    GoogleSignInClient googleSignInClient;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        circleImageView = findViewById(R.id.gmail_profile_pic);
        logout = findViewById(R.id.gmail_logout);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

//        progressDialog = new ProgressDialog(GoogleActivity.this);
//        progressDialog.setTitle("Loading data...");
//        progressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            //when firebase user is not equal to null
            //Set image on image view
            Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.profile).into(circleImageView);
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }

        //Initialze sign in client
        googleSignInClient = GoogleSignIn.getClient(GoogleActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sign out from google
                AlertDialog.Builder builder = new AlertDialog.Builder(GoogleActivity.this);
                builder.setTitle("Logout ?")
                        .setMessage("Do you really want to Logout ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        //check condition
                                        if (task.isSuccessful()) {
                                            //when task is successful
                                            //sign out from firebase
                                            firebaseAuth.signOut();
                                            Toast.makeText(GoogleActivity.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(GoogleActivity.this,LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(GoogleActivity.this, "Gmail Log out failed!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                        }).setNegativeButton("No", null);
                AlertDialog alertexit = builder.create();
                alertexit.show();
            }
        });

    }
}