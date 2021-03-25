package com.hisenberg.mygallery;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

class EntityImageAlbum {

    public String AlbumName;
    public String AlbumPath;
    public String FirstImagePath;
    public int AlbumFilesCount;

    public static ArrayList<EntityImageAlbum> getAllImageAlbums(Context ctx) {
        ArrayList<EntityImageAlbum> AlbumLists = new ArrayList();

        String[] properTiesToRetrieve = {
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
        };
        String ALBUM_GROUP_BY = "1) GROUP BY BUCKET_ID,(BUCKET_DISPLAY_NAME";
        Cursor cursor = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, properTiesToRetrieve, ALBUM_GROUP_BY, null, null);
        while (cursor.moveToNext()) {
            EntityImageAlbum meta = new EntityImageAlbum();
            meta.FirstImagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            meta.AlbumName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
            meta.AlbumPath = new File(meta.FirstImagePath).getParent();
            meta.AlbumFilesCount=EntityImageAlbumDetails.getImageAlbumDetail(ctx,meta.AlbumPath).size();
            AlbumLists.add(meta);
        }
        return AlbumLists;
    }
}
class ImageAlbumAdapter extends RecyclerView.Adapter<ImageAlbumViewHolder> {
    ArrayList<EntityImageAlbum> AlbumsData;
    AppCompatActivity ctx;

    public ImageAlbumAdapter(AppCompatActivity sourceCtx, ArrayList<EntityImageAlbum> sourceDataList) {
        AlbumsData = sourceDataList;
        ctx = sourceCtx;
    }

    @NonNull
    @Override
    public ImageAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.comp_albums, parent, false);
        ImageAlbumViewHolder vh = new ImageAlbumViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAlbumViewHolder viewHolder, int position) {
        try {
            final EntityImageAlbum CurrentData = AlbumsData.get(position);
            Glide.with(ctx).load(CurrentData.FirstImagePath).into(viewHolder.albumImage);
            viewHolder.albumName.setText(CurrentData.AlbumName);
            viewHolder.albumSize.setText(CurrentData.AlbumFilesCount+"");
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent AlbumIntent = new Intent(ctx, Activity_Image_AlbumDetails.class);
                    AlbumIntent.putExtra(Constants.IntExtraFolderUrl, CurrentData.AlbumPath);
                    ctx.startActivity(AlbumIntent);
                }

                ;
            });
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return AlbumsData.size();
    }
}
class ImageAlbumViewHolder extends RecyclerView.ViewHolder {
    public ImageView albumImage;// init the item view's
    public TextView albumName;
    public TextView albumSize;
    public View layout;

    public ImageAlbumViewHolder(View v) {
        super(v);
        layout = v;
        albumImage = v.findViewById(R.id.albumImg);
        albumName = v.findViewById(R.id.albumName);
        albumSize = v.findViewById(R.id.albumSize);
    }
}

class EntityImageAlbumDetails{
    public String ImageName;
    public String ImagePath;
    public static String FolderPath;

    public static ArrayList<EntityImageAlbumDetails> getImageAlbumDetail(Context ctx, String folderPath) {
        ArrayList<EntityImageAlbumDetails> AlbumImagesList = new ArrayList();
        FolderPath = folderPath;
        Cursor cursor = EntityImageHelper.GetAlbumByPath(ctx, folderPath);
        while (cursor.moveToNext()) {
            EntityImageAlbumDetails meta = new EntityImageAlbumDetails();
            meta.ImageName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
            meta.ImagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            AlbumImagesList.add(meta);
        }
        return AlbumImagesList;
    }
}
class ImageAlbumDetailsAdaptor extends RecyclerView.Adapter<ImageAlbumDetailViewHolder> {
    ArrayList<EntityImageAlbumDetails> AlbumData;
    AppCompatActivity ctx;

    public ImageAlbumDetailsAdaptor(AppCompatActivity sourceCtx, ArrayList<EntityImageAlbumDetails> sourceDataList) {
        AlbumData = sourceDataList;
        ctx = sourceCtx;
    }

    @NonNull
    @Override
    public ImageAlbumDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.comp_album_details, parent, false);
        ImageAlbumDetailViewHolder vh = new ImageAlbumDetailViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageAlbumDetailViewHolder viewHolder, final int position) {
        try {
            final EntityImageAlbumDetails CurrentData = AlbumData.get(position);
            Glide.with(ctx).load(CurrentData.ImagePath).into(viewHolder.albumDetailsImage);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ImageIntent = new Intent(ctx, Activity_Image_Detail.class);
                    ImageIntent.putExtra(Constants.IntExtraFileName, CurrentData.ImageName);
                    ImageIntent.putExtra(Constants.IntExtraPosition, position);
                    ImageIntent.putExtra(Constants.IntExtraFolderUrl, EntityImageAlbumDetails.FolderPath);
                    ActivityOptions transOption = ActivityOptions.makeSceneTransitionAnimation(ctx, viewHolder.albumDetailsImage, CurrentData.ImageName);
                    ctx.startActivity(ImageIntent, transOption.toBundle());
                }
            });
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return AlbumData.size();
    }
}
class ImageAlbumDetailViewHolder extends RecyclerView.ViewHolder {
    public ImageView albumDetailsImage;// init the item view's
    public View layout;

    public ImageAlbumDetailViewHolder(View v) {
        super(v);
        layout = v;
        albumDetailsImage = v.findViewById(R.id.albumDetailsImg);
    }
}

class EntityImageHelper{

    public static Cursor GetAlbumByPath(Context ctx, String folderPath) {
        String[] properTiesToRetrieve = {
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
        };
        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
        String selection = MediaStore.Images.Media.DATA + " like " + "'%" + folderPath + "/%'";
        Cursor albumCursor = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, properTiesToRetrieve, selection, null, orderBy);
        return albumCursor;
    }
}