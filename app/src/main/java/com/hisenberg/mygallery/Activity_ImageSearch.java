package com.hisenberg.mygallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class Activity_ImageSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity__image_search);

            Intent sourceIntent = getIntent();
            String imgUrl=Helper.GetImageShareUrl(sourceIntent);
            if (!imgUrl.equals("")) {
                BottomSheetHelper bottomSheetHelper=new BottomSheetHelper();
                bottomSheetHelper.show(getSupportFragmentManager(),imgUrl);
            }
        }
        catch (Exception ex){
            View MainLayout=findViewById(R.id.layoutImageSearch);
            Helper.ShowExceptionSnackbar(MainLayout,ex);
        }
    }
}
