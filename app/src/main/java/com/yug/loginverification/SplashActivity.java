package com.yug.loginverification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN =4000;

    Animation left_anim, right_anim,bottom_anim;
    TextView left_text,right_text,author_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        left_text= findViewById(R.id.left_text);
        right_text = findViewById(R.id.right_text);
        author_name = findViewById(R.id.author_name);

        left_anim = AnimationUtils.loadAnimation(this,R.anim.left_animation);
        right_anim = AnimationUtils.loadAnimation(this,R.anim.right_animation);
        bottom_anim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);


        left_text.setAnimation(left_anim);
        right_text.setAnimation(right_anim);
        author_name.setAnimation(bottom_anim);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}