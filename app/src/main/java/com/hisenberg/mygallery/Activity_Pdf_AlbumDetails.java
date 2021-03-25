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

public class Activity_Pdf_AlbumDetails extends AppCompatActivity {

    RecyclerView rcvPdfAlbumDetail;
    SwipeRefreshLayout refreshLayout;
    String folderPath="";
    View MainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity__pdf__album_details);
            MainLayout=findViewById(R.id.layoutPdfAlbumDetails);

            setupActionBar();

            rcvPdfAlbumDetail=findViewById(R.id.rcvPdfAlbumDetails);

            folderPath=getIntent().getStringExtra(Constants.IntExtraFolderUrl);
            setUpRecyclerView();

            refreshLayout=findViewById(R.id.layoutPdfAlbumDetailsRefresh);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    setUpRecyclerView();
                    rcvPdfAlbumDetail.getAdapter().notifyDataSetChanged();
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
        ArrayList<EntityPdfAlbumDetails> PdfList=EntityPdfAlbumDetails.getPdfAlbumDetail(this,folderPath);
        GridLayoutManager gridMgr=new GridLayoutManager(this,3);
        rcvPdfAlbumDetail.setLayoutManager(gridMgr);
        rcvPdfAlbumDetail.setAdapter(new PdfAlbumDetailsAdaptor(this,PdfList));
    }
}
