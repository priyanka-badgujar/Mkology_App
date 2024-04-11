package com.mkology.thelearningapp.splashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import com.mkology.thelearningapp.HomeActivity;
import com.mkology.thelearningapp.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView alpha_m;
    private ImageView alpha_k;
    private ImageView alpha_o1;
    private ImageView alpha_l;
    private ImageView alpha_o2;
    private ImageView alpha_g;
    private ImageView alpha_y;
    private Animation leftToRight;
    private List<ImageView> listImage = new ArrayList<>();
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        initialiseViews();
        addOtherListeners();
        listImage.get(i).startAnimation(leftToRight);
    }

    private void initialiseViews() {
        i = 0;
        alpha_m = findViewById(R.id.alpha_m);
        alpha_k = findViewById(R.id.alpha_k);
        alpha_o1 = findViewById(R.id.alpha_o1);
        alpha_l = findViewById(R.id.alpha_l);
        alpha_o2 = findViewById(R.id.alpha_o2);
        alpha_g = findViewById(R.id.alpha_g);
        alpha_y = findViewById(R.id.alpha_y);
        listImage.add(alpha_m);
        listImage.add(alpha_k);
        listImage.add(alpha_o1);
        listImage.add(alpha_l);
        listImage.add(alpha_o2);
        listImage.add(alpha_g);
        listImage.add(alpha_y);

        leftToRight = AnimationUtils.loadAnimation(this, R.anim.in_from_right);
    }

    private void addOtherListeners() {
        leftToRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listImage.get(i).setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(()-> {
                    if (i<listImage.size()-1) {
                        i++;
                        listImage.get(i).startAnimation(leftToRight);
                    } else {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    }
                }, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
