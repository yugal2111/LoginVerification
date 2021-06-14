package com.yug.loginverification;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookActivity;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FacebooklActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    Button logout;
    TextView name, email;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = firebaseAuth.getCurrentUser();

    String  Pfb_profileUrl;
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sahredprefs";
    public static final String FbprofileUrl = "PfbprofileUrl";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);


        circleImageView = findViewById(R.id.fb_profile_pic);
        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);




        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Pfb_profileUrl = sharedPreferences.getString(FbprofileUrl, "");

        if (user != null) {
            //when firebase user is not equal to null
            //Set image on image view
            Picasso.get().load(Pfb_profileUrl).placeholder(R.drawable.profile).into(circleImageView);
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(FacebooklActivity.this);
                builder.setTitle("Logout ?").setMessage("Do you really want to Logout ?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        firebaseAuth.signOut();
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();


                        Toast.makeText(FacebooklActivity.this, "Facebook Logged out successfully!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(FacebooklActivity.this, LoginActivity.class));
                        finish();
                    }
                }).setNegativeButton("No", null);
                AlertDialog alert_exit = builder.create();
                alert_exit.show();
            }
        });
    }

    AccessTokenTracker accessTokenTracker =  new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                circleImageView.setImageResource(0);
                name.setText(" ");
                email.setText(" ");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user ==null){
            openLogin();
        }
    }

    private void openLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}