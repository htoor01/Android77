package com.example.photos_cs213.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos_cs213.R;
import com.example.photos_cs213.adapters.PhotoAdapter;
import com.example.photos_cs213.model.Album;
import com.example.photos_cs213.model.DataManager;
import com.example.photos_cs213.model.Photo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AlbumDetailActivity extends AppCompatActivity implements PhotoAdapter.OnPhotoClickListener {

    private String albumName;
    private Album album;
    private DataManager dataManager;
    private PhotoAdapter adapter;
    
    private RecyclerView recyclerView;
    private TextView emptyState;
    private FloatingActionButton fabAdd;

    private final ActivityResultLauncher<String[]> photoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    // Persist permissions
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    
                    Photo newPhoto = new Photo(uri.toString());
                    if (album.addPhoto(newPhoto)) {
                        dataManager.saveAlbums();
                        refreshData();
                    } else {
                        Toast.makeText(this, "Photo already in album", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        albumName = getIntent().getStringExtra("ALBUM_NAME");
        dataManager = DataManager.getInstance(this);
        album = dataManager.getAlbum(albumName);

        if (album == null) {
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(albumName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recycler_photos);
        emptyState = findViewById(R.id.empty_state);
        fabAdd = findViewById(R.id.fab_add_photo);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        
        refreshData();

        fabAdd.setOnClickListener(v -> photoPickerLauncher.launch(new String[]{"image/*"}));
    }

    private void refreshData() {
        List<Photo> photos = album.getPhotos();
        if (adapter == null) {
            adapter = new PhotoAdapter(this, photos, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(photos);
        }

        if (photos.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPhotoClick(Photo photo, int position) {
        Intent intent = new Intent(this, PhotoDisplayActivity.class);
        intent.putExtra("ALBUM_NAME", albumName);
        intent.putExtra("PHOTO_POSITION", position);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Photo photo) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Photo")
                .setMessage("Are you sure you want to remove this photo from the album?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    album.removePhoto(photo);
                    dataManager.saveAlbums();
                    refreshData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}
