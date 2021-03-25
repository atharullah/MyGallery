package com.hisenberg.mygallery;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

public class Activity_Image_AlbumDetails extends AppCompatActivity {

    RecyclerView rcvAlbum;
    SwipeRefreshLayout refreshLayout;
    String folderPath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity__image__album_details);
            setupActionBar();
            rcvAlbum=findViewById(R.id.rcvAlbum);

            folderPath=getIntent().getStringExtra(Constants.IntExtraFolderUrl);
            setUpRecyclerView();

            refreshLayout=findViewById(R.id.layoutRefreshAlbumDetails);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    setUpRecyclerView();
                    rcvAlbum.getAdapter().notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }
            });
        }
        catch (Exception ex){
            View MainLayout=findViewById(R.id.layoutImgDetails);
            Helper.ShowExceptionSnackbar(MainLayout,ex);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            switch (item.getItemId()) {
                case android.R.id.home:
                    supportFinishAfterTransition();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        catch (Exception ex){
            View MainLayout=findViewById(R.id.layoutImgDetails);
            Helper.ShowExceptionSnackbar(MainLayout,ex);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpRecyclerView(){
        File folder=new File(folderPath);
        setTitle(folder.getName());
        ArrayList<EntityImageAlbumDetails> ImageList=EntityImageAlbumDetails.getImageAlbumDetail(this,folderPath);
        GridLayoutManager gridMgr=new GridLayoutManager(this,3);
        rcvAlbum.setLayoutManager(gridMgr);
        rcvAlbum.setAdapter(new ImageAlbumDetailsAdaptor(this,ImageList));
    }
}
