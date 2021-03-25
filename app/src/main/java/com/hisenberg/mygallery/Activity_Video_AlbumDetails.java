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

public class Activity_Video_AlbumDetails extends AppCompatActivity {

    RecyclerView rcvVideoAlbumDetail;
    SwipeRefreshLayout refreshLayout;
    String folderPath="";
    View MainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity__video__album_details);
            MainLayout=findViewById(R.id.layoutVideoAlbumDetails);

            setupActionBar();

            rcvVideoAlbumDetail=findViewById(R.id.rcvVideoAlbumDetails);

            folderPath=getIntent().getStringExtra(Constants.IntExtraFolderUrl);
            setUpRecyclerView();

            refreshLayout=findViewById(R.id.layoutVideoAlbumDetailsRefresh);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    setUpRecyclerView();
                    rcvVideoAlbumDetail.getAdapter().notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }
            });
        }
        catch (Exception ex){
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
        ArrayList<EntityVideoAlbumDetails> videoList=EntityVideoAlbumDetails.getVideoAlbumDetail(this,folderPath);
        GridLayoutManager gridMgr=new GridLayoutManager(this,3);
        rcvVideoAlbumDetail.setLayoutManager(gridMgr);
        rcvVideoAlbumDetail.setAdapter(new VideoAlbumDetailsAdaptor(this,videoList));
    }
}
