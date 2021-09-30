package com.example.doitchocolate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.doitchocolate.R;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static int splashInterval = 2000;

    private Animation frombottom, fromtop;
    private ImageView img_applogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img_applogo = (ImageView) findViewById(R.id.img_AppLogo);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        img_applogo.setAnimation(fromtop);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(SplashActivity.this, FrontActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }, splashInterval);
    }
}
