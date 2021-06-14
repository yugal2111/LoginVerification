package com.yug.loginverification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private Button get_otp, facebook_click, google_click, twitter_click;
    private EditText number;
    CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ProgressBar progressBar = findViewById(R.id.progressbar);

        //OTP Verification
        get_otp = findViewById(R.id.verify);
        number = findViewById(R.id.phone_number);


        get_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!number.getText().toString().trim().isEmpty()) {
                    if (number.getText().toString().trim().length() == 10) {

                        progressBar.setVisibility(View.VISIBLE);
                        get_otp.setVisibility(View.INVISIBLE);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + number.getText().toString(),
                                60,
                                TimeUnit.SECONDS,
                                LoginActivity.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                    @Override
                                    public void onVerificationCompleted(@NotNull PhoneAuthCredential credential) {
                                        progressBar.setVisibility(View.GONE);
                                        get_otp.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NotNull FirebaseException e) {
                                        // This callback is invoked in an invalid request for verification is made,
                                        // for instance if the the phone number format is not valid.
                                        progressBar.setVisibility(View.GONE);
                                        get_otp.setVisibility(View.VISIBLE);


                                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            // Invalid request
                                        } else if (e instanceof FirebaseTooManyRequestsException) {
                                            // The SMS quota for the project has been exceeded
                                            Toast.makeText(LoginActivity.this, "Daily Limit exceed", Toast.LENGTH_SHORT).show();
                                        }

                                        // Show a message and update the UI
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String verificationId,
                                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {

                                        progressBar.setVisibility(View.GONE);
                                        get_otp.setVisibility(View.VISIBLE);

                                        Toast.makeText(LoginActivity.this, "Code sent", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
                                        intent.putExtra("mobile", number.getText().toString());
                                        intent.putExtra("verificationId", verificationId);
                                        startActivity(intent);
                                    }
                                }
                        );
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter correct number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Facebook Login
        facebook_click = findViewById(R.id.facebook_click);
        callbackManager = CallbackManager.Factory.create();
        firebaseAuth = FirebaseAuth.getInstance();

        facebook_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));

                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        facebook_click.setVisibility(View.INVISIBLE);
                        google_click.setVisibility(View.INVISIBLE);
                        twitter_click.setVisibility(View.INVISIBLE);
                        get_otp.setVisibility(View.INVISIBLE);
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setTitle("Loading data.....");
                        progressDialog.show();

                        handleFacebookToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        facebook_click.setVisibility(View.VISIBLE);
                        google_click.setVisibility(View.VISIBLE);
                        twitter_click.setVisibility(View.VISIBLE);
                        get_otp.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Login cancelled.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        facebook_click.setVisibility(View.VISIBLE);
                        google_click.setVisibility(View.VISIBLE);
                        twitter_click.setVisibility(View.VISIBLE);
                        get_otp.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login error..", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


        twitter_click = findViewById(R.id.twitter_click);




        //Google API
        google_click = findViewById(R.id.google_click);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("169140552445-vj3m6crl7ut21f3hl4o2atu00pf8dsn7.apps.googleusercontent.com")
                .requestEmail().build();

        //Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);

        google_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize sign in intent
                Intent intent = googleSignInClient.getSignInIntent();
                //Start Activity for result
                startActivityForResult(intent, 100);
            }
        });

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //when user is already sign in
            //Redirect to profile activity
            startActivity(new Intent(LoginActivity.this, GoogleActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    //getFacebookToken
    private void handleFacebookToken(AccessToken Token) {
        Log.d("Facebook Authentication", "handleFacebookToken: " + Token);

        progressDialog.dismiss();

        AuthCredential credential = FacebookAuthProvider.getCredential(Token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, FacebooklActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Facebook CallbackManager
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);


        //getFacebookToken
        // check condition
        if (requestCode == 100) {
            //when request code is equal to 100
            //Initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            //Check condition
            if (signInAccountTask.isSuccessful()) {
                //when google sign in successful
                //Initalize string
                Toast.makeText(this, "Google sign in successful!", Toast.LENGTH_SHORT).show();
                //Initialize sign in account
                try {

                    GoogleSignInAccount googleSignInAccount = signInAccountTask
                            .getResult(ApiException.class);
                    facebook_click.setVisibility(View.INVISIBLE);
                    google_click.setVisibility(View.INVISIBLE);
                    twitter_click.setVisibility(View.INVISIBLE);
                    get_otp.setVisibility(View.INVISIBLE);
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setTitle("Loading data...");
                    progressDialog.show();
                    // check user
                    if (googleSignInAccount != null) {
                        //when sign in account is not euqal to null
                        //Initialize auth Credential
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        //check Credential
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                //Check condition
                                if (task.isSuccessful()) {
                                    //when task is successful
                                    //Redirect to GoogleActivity
                                    startActivity(new Intent(LoginActivity.this, GoogleActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    Toast.makeText(LoginActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    //when task is unsuccessful
                                    Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    facebook_click.setVisibility(View.VISIBLE);
                    google_click.setVisibility(View.VISIBLE);
                    twitter_click.setVisibility(View.VISIBLE);
                    get_otp.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }


}