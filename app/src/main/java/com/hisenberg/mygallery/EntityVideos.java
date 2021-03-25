package com.hisenberg.mygallery;

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

class EntityVideoAlbum {

    public String AlbumName;
    public String AlbumPath;
    public String FirstImagePath;
    public int AlbumFilesCount;

    public static ArrayList<EntityVideoAlbum> getAllVideoAlbums(Context ctx) {
        ArrayList<EntityVideoAlbum> AlbumLists = new ArrayList();

        String[] properTiesToRetrieve = {
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATA
        };
        String ALBUM_GROUP_BY = "1) GROUP BY BUCKET_ID,(BUCKET_DISPLAY_NAME";
        Cursor cursor = ctx.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, properTiesToRetrieve, ALBUM_GROUP_BY, null, null);
        while (cursor.moveToNext()) {
            EntityVideoAlbum meta = new EntityVideoAlbum();
            meta.FirstImagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
            meta.AlbumName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME));
            meta.AlbumPath = new File(meta.FirstImagePath).getParent();
            meta.AlbumFilesCount = EntityVideoHelper.GetAlbumByPath(ctx, meta.AlbumPath).getCount();
            AlbumLists.add(meta);
        }
        return AlbumLists;
    }
}
class VideoAlbumAdapter extends RecyclerView.Adapter<VideoAlbumViewHolder> {
    ArrayList<EntityVideoAlbum> AlbumsData;
    AppCompatActivity ctx;

    public VideoAlbumAdapter(AppCompatActivity sourceCtx, ArrayList<EntityVideoAlbum> sourceDataList) {
        AlbumsData = sourceDataList;
        ctx = sourceCtx;
    }

    @NonNull
    @Override
    public VideoAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.comp_albums, parent, false);
        VideoAlbumViewHolder vh = new VideoAlbumViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAlbumViewHolder viewHolder, int position) {
        try {
            final EntityVideoAlbum CurrentData = AlbumsData.get(position);
            Glide.with(ctx).load(CurrentData.FirstImagePath).into(viewHolder.albumImage);
            viewHolder.albumName.setText(CurrentData.AlbumName);
            viewHolder.albumSize.setText(CurrentData.AlbumFilesCount+"");
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent VideoAlbumDetailIntent = new Intent(ctx, Activity_Video_AlbumDetails.class);
                    VideoAlbumDetailIntent.putExtra(Constants.IntExtraFolderUrl, CurrentData.AlbumPath);
                    ctx.startActivity(VideoAlbumDetailIntent);
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
class VideoAlbumViewHolder extends RecyclerView.ViewHolder {
    public ImageView albumImage;// init the item view's
    public TextView albumName;
    public TextView albumSize;
    public View layout;

    public VideoAlbumViewHolder(View v) {
        super(v);
        layout = v;
        albumImage = v.findViewById(R.id.albumImg);
        albumName = v.findViewById(R.id.albumName);
        albumSize = v.findViewById(R.id.albumSize);
    }
}

class EntityVideoAlbumDetails{
    public String VideoName;
    public String VideoPath;
    public static String FolderPath;

    public static ArrayList<EntityVideoAlbumDetails> getVideoAlbumDetail(Context ctx, String folderPath) {
        ArrayList<EntityVideoAlbumDetails> AlbumImagesList = new ArrayList();
        FolderPath = folderPath;
        Cursor cursor = EntityVideoHelper.GetAlbumByPath(ctx, folderPath);
        while (cursor.moveToNext()) {
            EntityVideoAlbumDetails meta = new EntityVideoAlbumDetails();
            meta.VideoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME));
            meta.VideoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
            AlbumImagesList.add(meta);
        }
        return AlbumImagesList;
    }
}
class VideoAlbumDetailsAdaptor extends RecyclerView.Adapter<VideoAlbumDetailViewHolder> {
    ArrayList<EntityVideoAlbumDetails> AlbumData;
    AppCompatActivity ctx;

    public VideoAlbumDetailsAdaptor(AppCompatActivity sourceCtx, ArrayList<EntityVideoAlbumDetails> sourceDataList) {
        AlbumData = sourceDataList;
        ctx = sourceCtx;
    }

    @NonNull
    @Override
    public VideoAlbumDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.comp_video_thumb, parent, false);
        VideoAlbumDetailViewHolder vh = new VideoAlbumDetailViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoAlbumDetailViewHolder viewHolder, final int position) {
        try {
            final EntityVideoAlbumDetails CurrentData = AlbumData.get(position);
            Glide.with(ctx).load(CurrentData.VideoPath).into(viewHolder.imgThumb);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent videoIntent = new Intent(ctx, Activity_Video_Detail.class);
                    videoIntent.putExtra(Constants.IntExtraFileUrl, CurrentData.VideoPath);
                    videoIntent.putExtra(Constants.IntExtraFileName, CurrentData.VideoName);
                    videoIntent.putExtra(Constants.IntExtraPosition, position);
                    videoIntent.putExtra(Constants.IntExtraFolderUrl, CurrentData.FolderPath);
                    ctx.startActivity(videoIntent);
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
class VideoAlbumDetailViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgThumb;// init the item view's
    public ImageView imgPlay;// init the item view's
    public View layout;

    public VideoAlbumDetailViewHolder(View v) {
        super(v);
        layout = v;
        imgThumb = v.findViewById(R.id.compVideoThumbImg);
        imgPlay = v.findViewById(R.id.compVideoThumbPlay);
    }
}

class EntityVideoHelper{

    public static Cursor GetAlbumByPath(Context ctx, String folderPath) {
        String[] properTiesToRetrieve = {
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATA
        };
        String orderBy = MediaStore.Video.VideoColumns.DISPLAY_NAME;
        String selection = MediaStore.Video.Media.DATA + " like " + "'%" + folderPath + "/%'";
        Cursor videoCursor = ctx.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, properTiesToRetrieve, selection, null, orderBy);
        return videoCursor;
    }
}