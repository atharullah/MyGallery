package com.hisenberg.mygallery;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

class EntityImageDetails {
    public String ImageName;
    public String ImagePath;
    public static ArrayList<EntityImageDetails> getImages(Context ctx, String folderPath) {
        ArrayList<EntityImageDetails> ImagesList = new ArrayList<>();
        Cursor cursor = EntityImageHelper.GetAlbumByPath(ctx,folderPath);
        while (cursor.moveToNext()) {
            EntityImageDetails meta = new EntityImageDetails();
            meta.ImageName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
            meta.ImagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            ImagesList.add(meta);
        }
        return ImagesList;
    }
}

class ImageDetailsAdaptor extends RecyclerView.Adapter<ImageDetailViewHolder> {
    ArrayList<EntityImageDetails> AllImages;
    AppCompatActivity ctx;

    public ImageDetailsAdaptor(AppCompatActivity sourceCtx,ArrayList<EntityImageDetails> sourceDataList){
        AllImages=sourceDataList;
        ctx=sourceCtx;
    }

    @NonNull
    @Override
    public ImageDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.comp_image, parent, false);
        ImageDetailViewHolder vh = new ImageDetailViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageDetailViewHolder viewHolder, int position) {
        try{
            EntityImageDetails CurrentData=AllImages.get(position);
            Glide.with(ctx).load(CurrentData.ImagePath).into(viewHolder.photoView);
            ctx.setTitle(CurrentData.ImageName);
        }
        catch (Exception ex){
            System.out.print(ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return AllImages.size();
    }
}

class ImageDetailViewHolder extends RecyclerView.ViewHolder {
    public PhotoView photoView;
    public View layout;

    public ImageDetailViewHolder(View v) {
        super(v);
        layout=v;
        photoView =  v.findViewById(R.id.photoViewSingle);
    }
}

