package com.hoamz.hoamz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.hoamz.hoamz.R;
import com.hoamz.hoamz.data.model.Photo;
import com.hoamz.hoamz.utils.CameraUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.myViewHolder> {
    private List<Photo> photoList;

    public PhotoAdapter(){
        photoList = new ArrayList<>();
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo,parent,false);
        return new myViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        holder.bind(photo);

        holder.lnPhoto.setOnClickListener(v ->{
            onClickPhoto.clickPhoto(photo);
        });
    }


    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imagePhoto;
        private final LinearLayout lnPhoto;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePhoto = itemView.findViewById(R.id.ivItemPhoto);
            lnPhoto = itemView.findViewById(R.id.lnPhoto);
        }
        public void bind(Photo photo){
            Glide.with(imagePhoto.getContext())
                    .load(photo.getUri())
                    .into(imagePhoto);
        }
    }

    public interface onClickPhoto{
        void clickPhoto(Photo photo);
    }

    public onClickPhoto onClickPhoto;

    public void setOnClickPhoto(onClickPhoto onClickPhoto) {
        this.onClickPhoto = onClickPhoto;
    }
}
