package com.example.imagesearchapp;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageRVAdapter extends RecyclerView.Adapter<ImageRVAdapter.ViewHolder> {

    List<Bitmap> imageList;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public ImageRVAdapter(List<Bitmap> imageList){
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap image = imageList.get(position);
        holder.imageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


}
