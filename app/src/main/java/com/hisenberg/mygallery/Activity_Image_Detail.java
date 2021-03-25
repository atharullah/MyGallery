package com.hisenberg.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class Activity_Image_Detail extends AppCompatActivity {

    BottomSheetBehavior sheetBehavior;
    View CurrentMainLayout;
    RecyclerView RcvImageDetails;
    ArrayList<EntityImageDetails> ImageList;
    int currentImgPosition;
    FloatingActionButton fab;
    String ImageTitle;
    View persBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity__image__detail);
            CurrentMainLayout=findViewById(R.id.layoutSingleImage);
            Toolbar toolbar=findViewById(R.id.my_toolbar);
            RcvImageDetails = findViewById(R.id.RcvImageDetails);
            fab=findViewById(R.id.fabImgDetails);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            ImageList = new ArrayList<>();
            ImageTitle = "";
            Intent currentIntent = getIntent();
            setupRecycleData(currentIntent);
            currentImgPosition = currentIntent.getIntExtra(Constants.IntExtraPosition, 0);
            setTitle(ImageTitle);
            setupRecyclerView();

            persBottomSheet=findViewById(R.id.imgDetailsBottomSheet);
            final BottomSheetBehavior behavior = BottomSheetBehavior.from(persBottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(behavior.getState()==BottomSheetBehavior.STATE_COLLAPSED)
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    else
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });

        } catch (Exception ex) {
            Helper.ShowExceptionSnackbar(CurrentMainLayout, ex);
        }
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try{
            getMenuInflater().inflate(R.menu.action_bar_menu,menu);
            return true;
        }
        catch (Exception ex){
            Helper.ShowExceptionSnackbar(CurrentMainLayout, ex);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            switch (item.getItemId()) {
                case android.R.id.home:
                    supportFinishAfterTransition();
                    return true;
                case R.id.action_share:
                    ShareImage();
                    return true;
                case R.id.action_delete:
                    DeleteImage();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        catch (Exception ex){
            Helper.ShowExceptionSnackbar(CurrentMainLayout, ex);
        }
        return false;
    }

    public void ShareImage(){
        File file = new File(ImageList.get(currentImgPosition).ImagePath);
        Uri uri =FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent .setType("image/*");
        intent .putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(intent);
    }

    public void DeleteImage(){
        MaterialAlertDialogBuilder Dialog= new MaterialAlertDialogBuilder(this);
        Dialog.setTitle("Confirm");
        Dialog.setMessage("Are you sure you want to delete");
        Dialog.setPositiveButton("Yes",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                EntityImageDetails currentImgData = ImageList.get(currentImgPosition);
                File imgFile = new File(currentImgData.ImagePath);
                if(imgFile.isFile() && imgFile.exists()){
                    boolean isDelete = imgFile.delete();
                    if (isDelete) {
                        ImageList.remove(currentImgPosition);
                        RcvImageDetails.getAdapter().notifyItemRemoved(currentImgPosition);
                        RcvImageDetails.getAdapter().notifyItemRangeChanged(currentImgPosition,ImageList.size());
                        Helper.ShowMessageSnackbar(CurrentMainLayout, "file deleted");
                    }
                }
                else {
                    RcvImageDetails.getAdapter().notifyItemRemoved(currentImgPosition);
                    RcvImageDetails.getAdapter().notifyItemRangeChanged(currentImgPosition,ImageList.size());
                }
            }
        });
        Dialog.setNegativeButton("No",null);
        Dialog.show();
    }

    public void setupRecyclerView() {
        LinearLayoutManager linearMgr = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        RcvImageDetails.setLayoutManager(linearMgr);
        RcvImageDetails.setAdapter(new ImageDetailsAdaptor(this, ImageList));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(RcvImageDetails);
        RcvImageDetails.getLayoutManager().scrollToPosition(currentImgPosition);
        RcvImageDetails.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    currentImgPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                }
            }
        });

    }

    public void setupRecycleData(Intent currentIntent){

        if (currentIntent.getType() != null) {
            ClipData ClipData = currentIntent.getClipData();
            if (ClipData.getItemCount() > 0) {
                for (int i = 0; i < ClipData.getItemCount(); i++) {
                    EntityImageDetails imgDetail = new EntityImageDetails();
                    android.content.ClipData.Item dataItem = ClipData.getItemAt(i);
                    if (dataItem.getUri() != null) {
                        String imgPath = dataItem.getUri().toString();
                        if (i == 0)
                            ImageTitle = imgPath.substring(imgPath.lastIndexOf("/") + 1);
                        imgDetail.ImageName = ImageTitle;
                        imgDetail.ImagePath = dataItem.getUri().toString();
                    } else if (dataItem.getText() != null) {
                        String imgPath = dataItem.getText().toString();
                        if (i == 0)
                            ImageTitle = imgPath.substring(imgPath.lastIndexOf("/") + 1);
                        imgDetail.ImageName = ImageTitle;
                        imgDetail.ImagePath = dataItem.getText().toString();
                    }
                    ImageList.add(imgDetail);
                }
            }
        } else {
            String folderPath = currentIntent.getStringExtra(Constants.IntExtraFolderUrl);
            ImageTitle = currentIntent.getStringExtra(Constants.IntExtraFileName);
            ImageList = EntityImageDetails.getImages(this, folderPath);
        }
    }
}
