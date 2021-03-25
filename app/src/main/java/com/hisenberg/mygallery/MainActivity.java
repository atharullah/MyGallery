package com.hisenberg.mygallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    View cardviewImages, cardviewVideos, cardviewPdf, cardviewOffice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardviewImages=findViewById(R.id.cardviewMainImages);
        cardviewOffice=findViewById(R.id.cardviewMainOffice);
        cardviewPdf=findViewById(R.id.cardviewMainPdf);
        cardviewVideos=findViewById(R.id.cardviewMainVideos);

        cardviewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageLibraryIntent = new Intent(MainActivity.this, Activity_Image_Albums.class);
                startActivity(imageLibraryIntent);
            }
        });

        cardviewVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoLibraryIntent = new Intent(MainActivity.this, Activity_Video_Albums.class);
                startActivity(videoLibraryIntent);
            }
        });

        cardviewPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pdfLibraryIntent = new Intent(MainActivity.this, Activity_Pdf_Albums.class);
                startActivity(pdfLibraryIntent);
            }
        });
    }
}


