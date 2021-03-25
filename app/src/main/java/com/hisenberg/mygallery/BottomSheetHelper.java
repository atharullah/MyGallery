package com.hisenberg.mygallery;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BottomSheetHelper extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bottomShetView=inflater.inflate(R.layout.comp_bottomsheet_imgsearch,container,false);
        ImageView imgSearchView=bottomShetView.findViewById(R.id.bottomSheetImgSerachImgView);
        Glide.with(inflater.getContext()).load(Uri.parse(getTag())).into(imgSearchView);
        return  bottomShetView;
    }
}
