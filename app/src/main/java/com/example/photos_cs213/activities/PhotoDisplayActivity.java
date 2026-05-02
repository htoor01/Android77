package com.example.photos_cs213.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.photos_cs213.R;
import com.example.photos_cs213.model.Album;
import com.example.photos_cs213.model.DataManager;
import com.example.photos_cs213.model.Photo;
import com.example.photos_cs213.model.Tag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PhotoDisplayActivity extends AppCompatActivity {

    private String albumName;
    private int currentPosition;
    private Album currentAlbum;
    private List<Photo> photos;
    private DataManager dataManager;

    private ImageView imageFull;
    private ChipGroup chipGroupTags;
    private Button btnPrevious;
    private Button btnNext;
    private TextView photoPosition;
    private FloatingActionButton fabAddTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        albumName = getIntent().getStringExtra("ALBUM_NAME");
        currentPosition = getIntent().getIntExtra("PHOTO_POSITION", 0);

        dataManager = DataManager.getInstance(this);
        currentAlbum = dataManager.getAlbum(albumName);

        if (currentAlbum == null) {
            finish();
            return;
        }

        photos = currentAlbum.getPhotos();
        if (photos.isEmpty()) {
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(albumName);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        imageFull = findViewById(R.id.image_full);
        chipGroupTags = findViewById(R.id.chip_group_tags);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        photoPosition = findViewById(R.id.photo_position);
        fabAddTag = findViewById(R.id.fab_add_tag);

        btnPrevious.setOnClickListener(v -> {
            if (currentPosition > 0) {
                currentPosition--;
                loadPhoto();
                displayTags();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPosition < photos.size() - 1) {
                currentPosition++;
                loadPhoto();
                displayTags();
            }
        });

        fabAddTag.setOnClickListener(v -> showAddTagDialog());

        loadPhoto();
        displayTags();
    }

    private void loadPhoto() {
        Photo photo = photos.get(currentPosition);
        try {
            Uri uri = Uri.parse(photo.getUriString());
            InputStream input = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            imageFull.setImageBitmap(bitmap);
            if (input != null) input.close();
        } catch (Exception e) {
            e.printStackTrace();
            imageFull.setImageResource(android.R.drawable.ic_menu_report_image);
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }

        photoPosition.setText((currentPosition + 1) + " / " + photos.size());
        btnPrevious.setEnabled(currentPosition > 0);
        btnNext.setEnabled(currentPosition < photos.size() - 1);
    }

    private void displayTags() {
        chipGroupTags.removeAllViews();
        Photo photo = photos.get(currentPosition);
        for (Tag tag : photo.getTags()) {
            Chip chip = new Chip(this);
            chip.setText(tag.toString());
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                photo.removeTag(tag);
                dataManager.saveAlbums();
                displayTags();
            });
            chipGroupTags.addView(chip);
        }
    }

    private void showAddTagDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_tag, null);
        Spinner spinner = view.findViewById(R.id.spinner_tag_type);
        EditText editText = view.findViewById(R.id.edit_tag_value);

        new AlertDialog.Builder(this)
                .setTitle("Add Tag")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String type = spinner.getSelectedItem().toString();
                    String value = editText.getText().toString().trim();
                    if (!value.isEmpty()) {
                        Photo photo = photos.get(currentPosition);
                        Tag newTag = new Tag(type, value);
                        if (photo.addTag(newTag)) {
                            dataManager.saveAlbums();
                            displayTags();
                        } else {
                            Toast.makeText(this, "Tag already exists", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Value cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_move) {
            showMovePhotoDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMovePhotoDialog() {
        List<Album> allAlbums = dataManager.getAllAlbums();
        List<String> otherAlbumNames = new ArrayList<>();
        List<Album> otherAlbums = new ArrayList<>();

        for (Album a : allAlbums) {
            if (!a.getName().equalsIgnoreCase(albumName)) {
                otherAlbumNames.add(a.getName());
                otherAlbums.add(a);
            }
        }

        if (otherAlbumNames.isEmpty()) {
            Toast.makeText(this, "No other albums to move to", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] namesArray = otherAlbumNames.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("Move Photo to...")
                .setItems(namesArray, (dialog, which) -> {
                    Album destAlbum = otherAlbums.get(which);
                    Photo currentPhoto = photos.get(currentPosition);
                    if (dataManager.movePhoto(currentPhoto, currentAlbum, destAlbum)) {
                        Toast.makeText(this, "Photo moved", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to move photo", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
