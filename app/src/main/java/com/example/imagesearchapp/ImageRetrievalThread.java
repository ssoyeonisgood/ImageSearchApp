package com.example.imagesearchapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class ImageRetrievalThread extends Thread {

    private RemoteUtilities remoteUtilities;
    private SearchResponseViewModel sViewModel;
    private ImageViewModel imageViewModel;
    private ErrorViewModel errorViewModel;
    private Activity uiActivity;
    public List<ImageViewModel> imageList;

    public ImageRetrievalThread(Activity uiActivity, SearchResponseViewModel viewModel, ImageViewModel imageViewModel, ErrorViewModel errorViewModel) {
        remoteUtilities = RemoteUtilities.getInstance(uiActivity);
        this.sViewModel = viewModel;
        this.imageViewModel = imageViewModel;
        this.errorViewModel = errorViewModel;
        this.uiActivity=uiActivity;
    }
    public void run(){
        List<String> endpoint = getEndpoint(sViewModel.getResponse());
        if(endpoint==null){
            uiActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(uiActivity,"No image found",Toast.LENGTH_LONG).show();
                    errorViewModel.setErrorCode(errorViewModel.getErrorCode()+1);
                }
            });
        }
        else {
            imageViewModel.clearImageList();
            for (int i = 0; i < endpoint.size(); i++){
                Bitmap image = getImageFromUrl(endpoint.get(i));
                imageViewModel.addImage(image);
            }
//            Bitmap image = getImageFromUrl(endpoint);
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
            }
//            imageViewModel.setImage(image);
            }
    }

    private List<String> getEndpoint(String data){
        List<String> imageUrls= new ArrayList<>();
        try {
            JSONObject jBase = new JSONObject(data);
            JSONArray jHits = jBase.getJSONArray("hits");
            if(jHits.length()>0) {
                if (jHits.length() >= 15) {
                    for (int i = 0; i < 15; i++) {
                        JSONObject jHitsItem = jHits.getJSONObject(i);
                        imageUrls.add(jHitsItem.getString("previewURL"));
                    }
                }
                if (jHits.length() < 15) {
                    for (int i = 0; i < jHits.length(); i++) {
                        JSONObject jHitsItem = jHits.getJSONObject(i);
                        imageUrls.add(jHitsItem.getString("previewURL"));
                    }
                }
            }
            else {
                imageUrls = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrls;
    }

    private Bitmap getImageFromUrl(String imageUrl){
        Bitmap image = null;
        Uri.Builder url = Uri.parse(imageUrl).buildUpon();
        String urlString = url.build().toString();
        HttpURLConnection connection = remoteUtilities.openConnection(urlString);
        if(connection!=null){
            if(remoteUtilities.isConnectionOkay(connection)==true){
                image = getBitmapFromConnection(connection);
                connection.disconnect();
            }
        }
        return image;
    }

    public Bitmap getBitmapFromConnection(HttpURLConnection conn){
        Bitmap data = null;
        try {
            InputStream inputStream = conn.getInputStream();
            byte[] byteData = getByteArrayFromInputStream(inputStream);
            data = BitmapFactory.decodeByteArray(byteData,0,byteData.length);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }

    private byte[] getByteArrayFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

}
