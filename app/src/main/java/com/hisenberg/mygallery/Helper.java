package com.hisenberg.mygallery;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.core.content.FileProvider;

public class Helper {

    public static void ShowExceptionSnackbar(View TopLayout, Exception ex) {
        Snackbar snackbar = Snackbar.make(TopLayout, "Excpetion:\n" + ex.getMessage(), Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    public static void ShowMessageSnackbar(View TopLayout, String msg) {
        Snackbar snackbar = Snackbar.make(TopLayout, msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static String GetImageShareUrl(Intent sourceIntent){
        String imgUrl="";
        if (sourceIntent.getType() != null) {
            ClipData ClipData = sourceIntent.getClipData();
            if (ClipData.getItemCount() > 0) {
                for (int i = 0; i < ClipData.getItemCount(); i++) {
                    android.content.ClipData.Item dataItem = ClipData.getItemAt(i);
                    if (dataItem.getUri() != null) {
                        imgUrl= dataItem.getUri().toString();
                    }
                }
            }
        }
        return imgUrl;
    }
}

class Constants {
    public static String IntExtraFileUrl = "FileUrl";
    public static String IntExtraFolderUrl = "FolderUrl";
    public static String IntExtraFileName = "FileName";
    public static String IntExtraPosition = "Position";
}
