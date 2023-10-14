package com.example.imagesearchapp;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ImageViewModel  extends ViewModel {

    public List<Bitmap> imageList;
    public MutableLiveData<List<Bitmap>> imageListLiveData;
    public ImageViewModel(){
        this.imageList = new ArrayList<Bitmap>();
        this.imageListLiveData = new MutableLiveData<List<Bitmap>>(imageList);
    }
    public List<Bitmap> getImageList() {
        return imageListLiveData.getValue();
    }

    public void addImage(Bitmap bitmap) {
        this.imageList.add(bitmap);
        this.imageListLiveData.postValue(this.imageList);
    }
}
