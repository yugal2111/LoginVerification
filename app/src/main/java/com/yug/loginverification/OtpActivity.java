package com.yug.loginverification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private Button verify;
    private String verificationId;
    private EditText input_Code1, input_Code2, input_Code3, input_Code4, input_Code5, input_Code6;
    private TextView get_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        get_number = findViewById(R.id.get_number);

        verify = findViewById(R.id.verify);
        final ProgressBar progressBar = findViewById(R.id.progressbar);
        get_number.setText(String.format("+91-%s", getIntent().getStringExtra("mobile")));

        input_Code1 = findViewById(R.id.input_code1);
        input_Code2 = findViewById(R.id.input_code2);
        input_Code3 = findViewById(R.id.input_code3);
        input_Code4 = findViewById(R.id.input_code4);
        input_Code5 = findViewById(R.id.input_code5);
        input_Code6 = findViewById(R.id.input_code6);

        verificationId = getIntent().getStringExtra("verificationId");

        verify.setOnClickListener(v -> {
            if (!input_Code1.getText().toString().trim().isEmpty() && !input_Code2.getText().toString().trim().isEmpty() && !input_Code3.getText().toString().trim().isEmpty() && !input_Code4.getText().toString().trim().isEmpty() && !input_Code5.getText().toString().trim().isEmpty() && !input_Code6.getText().toString().trim().isEmpty()) {
                String code = input_Code1.getText().toString() +
                        input_Code2.getText().toString() +
                        input_Code3.getText().toString() +
                        input_Code4.getText().toString() +
                        input_Code5.getText().toString() +
                        input_Code6.getText().toString();
                if (verificationId != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    verify.setVisibility(View.VISIBLE);

                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(OtpActivity.this, "Enter the correct otp", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(this, "Please check internet conncetion", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter all number", Toast.LENGTH_SHORT).show();
            }

        });

        setupOTPInputs();

        findViewById(R.id.resend_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        OtpActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                // This callback will be invoked in two situations:
                                // 1 - Instant verification. In some cases the phone number can be instantly
                                //     verified without needing to send or enter a verification code.
                                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                //     detect the incoming verification SMS and perform verification without
                                //     user action.
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                // This callback is invoked in an invalid request for verification is made,
                                // for instance if the the phone number format is not valid.


                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    // Invalid request
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    // The SMS quota for the project has been exceeded
                                    Toast.makeText(OtpActivity.this, "Daily Limit exceed", Toast.LENGTH_SHORT).show();
                                }

                                // Show a message and update the UI
                            }

                            @Override
                            public void onCodeSent(@NonNull String newverificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                                // The SMS verification code has been sent to the provided phone number, we
                                // now need to ask the user to enter the code and then construct a credential
                                // by combining the code with a verification ID.

                                verificationId = newverificationId;
                                Toast.makeText(OtpActivity.this, "Otp send ", Toast.LENGTH_SHORT).show();

                            }
                        }
                );

            }
        });
    }

    private void setupOTPInputs() {
        input_Code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input_Code2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        input_Code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input_Code3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        input_Code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input_Code4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        input_Code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input_Code5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        input_Code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    input_Code6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}