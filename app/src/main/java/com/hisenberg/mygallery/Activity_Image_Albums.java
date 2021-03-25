package com.hisenberg.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Activity_Image_Albums extends AppCompatActivity {

    static final int PermissionStatus = 1;
    static String[] Permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    View MainLayout;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity__image__albums);
            MainLayout = findViewById(R.id.layoutAllAlbums);
            recyclerView = findViewById(R.id.rcvAllFolders);
            CheckPerm();

            refreshLayout=findViewById(R.id.layoutRefreshAllAlbums);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    CheckPerm();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }
            });

        } catch (Exception ex) {
            Helper.ShowExceptionSnackbar(MainLayout, ex);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            switch (requestCode) {
                case PermissionStatus:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        GetGalleryData();
                    else
                        PermSnackBar(MainLayout);
                    return;
            }
        } catch (Exception ex) {
            Helper.ShowExceptionSnackbar(MainLayout, ex);
        }
    }

    void GetGalleryData() {
        ArrayList<EntityImageAlbum> AllAlbums = EntityImageAlbum.getAllImageAlbums(this);
        GridLayoutManager gridMgr = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridMgr);
        recyclerView.setAdapter(new ImageAlbumAdapter(this, AllAlbums));
    }

    void CheckPerm() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //User Denied Request Previously
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                PermSnackBar(MainLayout);
                //User requested don't ask for permission
            else
                ActivityCompat.requestPermissions(this, Permissions, PermissionStatus);
        } else
            GetGalleryData();
    }

    void PermSnackBar(View layout) {
        Snackbar bootomBar = Snackbar.make(layout, "Storage Permission Needed To Show Images", Snackbar.LENGTH_INDEFINITE);
        bootomBar.setAction("Grant", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(Activity_Image_Albums.this, Permissions, PermissionStatus);
            }
        });
        bootomBar.show();
    }
}
