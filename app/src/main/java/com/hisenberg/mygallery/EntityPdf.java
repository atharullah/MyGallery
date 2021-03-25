package com.hisenberg.mygallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

class EntityPdfAlbum {

    public String AlbumName;
    public String AlbumPath;
    public int AlbumFilesCount;

    public static ArrayList<EntityPdfAlbum> getAllPdfAlbums(Context ctx) {
        ArrayList<EntityPdfAlbum> AlbumLists = new ArrayList();

        String[] properTiesToRetrieve = {
                MediaStore.Files.FileColumns.PARENT,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME
        };
        String ALBUM_GROUP_BY = MediaStore.Files.FileColumns.MIME_TYPE + "='"
                                    +MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
                                    +"') GROUP BY (PARENT";

        //String ALBUM_GROUP_BY =MediaStore.Files.FileColumns.DATA + " like " + "'%.pdf') GROUP BY (PARENT";
        Cursor cursor = ctx.getContentResolver().query(MediaStore.Files.getContentUri("external"), properTiesToRetrieve, ALBUM_GROUP_BY, null, null);

        while (cursor.moveToNext()) {
            EntityPdfAlbum meta = new EntityPdfAlbum();
            //meta.AlbumName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.PARENT));
            String firstFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            meta.AlbumPath = new File(firstFilePath).getParent();
            meta.AlbumName=new File(firstFilePath).getParentFile().getName();
            meta.AlbumFilesCount = EntityPdfHelper.GetPdfAlbumByPath(ctx, meta.AlbumPath).getCount();
            AlbumLists.add(meta);
        }
        return AlbumLists;
    }


}
class PdfAlbumAdapter extends RecyclerView.Adapter<PdfAlbumViewHolder> {
    ArrayList<EntityPdfAlbum> AlbumsData;
    AppCompatActivity ctx;

    public PdfAlbumAdapter(AppCompatActivity sourceCtx, ArrayList<EntityPdfAlbum> sourceDataList) {
        AlbumsData = sourceDataList;
        ctx = sourceCtx;
    }

    @NonNull
    @Override
    public PdfAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.comp_albums, parent, false);
        PdfAlbumViewHolder vh = new PdfAlbumViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PdfAlbumViewHolder viewHolder, int position) {
        try {
            final EntityPdfAlbum CurrentData = AlbumsData.get(position);
            viewHolder.albumName.setText(CurrentData.AlbumName);
            viewHolder.albumSize.setText(CurrentData.AlbumFilesCount+"");
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent pdfAlbumDetailIntent = new Intent(ctx, Activity_Pdf_AlbumDetails.class);
                    pdfAlbumDetailIntent.putExtra(Constants.IntExtraFolderUrl, CurrentData.AlbumPath);
                    ctx.startActivity(pdfAlbumDetailIntent);
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
class PdfAlbumViewHolder extends RecyclerView.ViewHolder {
    public ImageView albumImage;// init the item view's
    public TextView albumName;
    public TextView albumSize;
    public View layout;

    public PdfAlbumViewHolder(View v) {
        super(v);
        layout = v;
        albumImage = v.findViewById(R.id.albumImg);
        albumName = v.findViewById(R.id.albumName);
        albumSize = v.findViewById(R.id.albumSize);
    }
}

class EntityPdfAlbumDetails{
    public String PdfName;
    public String PdfPath;
    public static String FolderPath;

    public static ArrayList<EntityPdfAlbumDetails> getPdfAlbumDetail(Context ctx, String folderPath) {
        ArrayList<EntityPdfAlbumDetails> AlbumImagesList = new ArrayList();
        FolderPath = folderPath;
        Cursor cursor = EntityPdfHelper.GetPdfAlbumByPath(ctx, folderPath);
        while (cursor.moveToNext()) {
            EntityPdfAlbumDetails meta = new EntityPdfAlbumDetails();
            meta.PdfName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
            meta.PdfPath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            AlbumImagesList.add(meta);
        }
        return AlbumImagesList;
    }
}
class PdfAlbumDetailsAdaptor extends RecyclerView.Adapter<PdfAlbumDetailViewHolder> {
    ArrayList<EntityPdfAlbumDetails> AlbumData;
    AppCompatActivity ctx;

    public PdfAlbumDetailsAdaptor(AppCompatActivity sourceCtx, ArrayList<EntityPdfAlbumDetails> sourceDataList) {
        AlbumData = sourceDataList;
        ctx = sourceCtx;
    }

    @NonNull
    @Override
    public PdfAlbumDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.comp_pdf_album_detail, parent, false);
        PdfAlbumDetailViewHolder vh = new PdfAlbumDetailViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final PdfAlbumDetailViewHolder viewHolder, final int position) {
        try {
            final EntityPdfAlbumDetails CurrentData = AlbumData.get(position);
            Glide.with(ctx).load(R.drawable.vector_pdf_40dp).into(viewHolder.imgThumb);
            viewHolder.txtPdfName.setText(CurrentData.PdfName);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pdfIntent = new Intent(ctx, Activity_Pdf_Detail.class);
                    pdfIntent.putExtra(Constants.IntExtraFileUrl, CurrentData.PdfPath);
                    pdfIntent.putExtra(Constants.IntExtraPosition, position);
                    pdfIntent.putExtra(Constants.IntExtraFolderUrl, CurrentData.FolderPath);
                    ctx.startActivity(pdfIntent);
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
class PdfAlbumDetailViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgThumb;// init the item view's
    public View layout;
    public TextView txtPdfName;

    public PdfAlbumDetailViewHolder(View v) {
        super(v);
        layout = v;
        imgThumb = v.findViewById(R.id.compPdfAlbumDetailsImg);
        txtPdfName = v.findViewById(R.id.compPdfAlbumDetailsFileName);
    }
}

class EntityPdfHelper{

    public static Cursor GetPdfAlbumByPath(Context ctx, String folderPath) {
        String[] properTiesToRetrieve = {
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA
        };
        String orderBy = MediaStore.Files.FileColumns.DISPLAY_NAME;
        String selection =MediaStore.Files.FileColumns.MIME_TYPE + "='"
                +MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
                +"' AND "+ MediaStore.Files.FileColumns.DATA + " like '" + folderPath + "/%'";
        Cursor pdfCursor = ctx.getContentResolver().query(MediaStore.Files.getContentUri("external"), properTiesToRetrieve, selection, null, orderBy);
        return pdfCursor;
    }
}
