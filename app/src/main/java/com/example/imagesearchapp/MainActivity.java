package com.example.imagesearchapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageRVAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    SearchResponseViewModel sViewModel;
    ImageViewModel imageViewModel;
    ErrorViewModel errorViewModel;
    Button loadImage;
    ProgressBar progressBar;
    EditText searchKey;
    Menu menu;
    ViewType selectedViewType = ViewType.OneColumn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        sViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(SearchResponseViewModel.class);
        imageViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(ImageViewModel.class);
        errorViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(ErrorViewModel.class);

        recyclerView = findViewById(R.id.imageRV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImageRVAdapter(imageViewModel.getImageList(), selectedViewType);
        recyclerView.setAdapter(adapter);

        loadImage = findViewById(R.id.loadImage);
        progressBar = findViewById(R.id.progressBarId);
        searchKey = findViewById(R.id.inputSearch);

        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.INVISIBLE);
                String searchValues = searchKey.getText().toString();
                APISearchThread searchThread = new APISearchThread(searchValues,MainActivity.this,sViewModel);
                progressBar.setVisibility(View.VISIBLE);
                searchThread.start();
            }
        });

        sViewModel.response.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Search Complete",Toast.LENGTH_LONG).show();
                ImageRetrievalThread imageRetrievalThread = new ImageRetrievalThread(MainActivity.this, sViewModel, imageViewModel, errorViewModel);
                progressBar.setVisibility(View.VISIBLE);
                imageRetrievalThread.start();

            }
        });


        imageViewModel.imageListLiveData.observe(this, new Observer<List<Bitmap>>() {
            @Override
            public void onChanged(List<Bitmap> ImageList) {
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
        errorViewModel.errorCode.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.column_ChangeView){
                changeSelectedViewType();
                changeMenuItemIcon();
                changeRecyclerViewViewType();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeRecyclerViewViewType() {
        switch (selectedViewType){
            case OneColumn:
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                adapter = new ImageRVAdapter(imageViewModel.getImageList(), selectedViewType);
                recyclerView.setAdapter(adapter);
                break;
            case TwoColumns:
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                adapter = new ImageRVAdapter(imageViewModel.getImageList(), selectedViewType);
                recyclerView.setAdapter(adapter);
                break;

        }

    }

    private void changeMenuItemIcon() {
        switch (selectedViewType){
            case OneColumn:
            menu.findItem(R.id.column_ChangeView).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.two_column));
                break;
            case TwoColumns:
                menu.findItem(R.id.column_ChangeView).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.one_column));
                break;


        }
    }

    private void changeSelectedViewType() {
        switch (selectedViewType){
            case OneColumn:
                selectedViewType = ViewType.TwoColumns;
                break;
            case TwoColumns:
                selectedViewType = ViewType.OneColumn;
                break;
        }
    }
}