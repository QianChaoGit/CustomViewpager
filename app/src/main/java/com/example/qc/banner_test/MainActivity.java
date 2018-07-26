package com.example.qc.banner_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private int[] img = {R.drawable.guide_01, R.drawable.guide_02, R.drawable.guide_03};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BannerView bannerView = findViewById(R.id.banner);
        ArrayList<String> imgUrls = new ArrayList();
        for (int i = 0; i < img.length; i++) {
            imgUrls.add(String.valueOf(img[i]));
        }
        bannerView.setImageResources(imgUrls, new BannerView.ImageCycleViewListener() {
            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                imageView.setImageResource(Integer.valueOf(imageURL));
            }

            @Override
            public void onImageClick(int position, View imageView) {
                Toast.makeText(MainActivity.this, "position=" + position, Toast.LENGTH_SHORT).show();
            }
        });
        bannerView.startImageCycle();

    }

}
