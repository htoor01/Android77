package com.example.photos_cs213.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos_cs213.R;
import com.example.photos_cs213.adapters.PhotoAdapter;
import com.example.photos_cs213.model.Album;
import com.example.photos_cs213.model.DataManager;
import com.example.photos_cs213.model.Photo;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements PhotoAdapter.OnPhotoClickListener {

    private DataManager dataManager;
    private Spinner spinnerTagType1, spinnerTagType2;
    private AutoCompleteTextView autoCompleteTag1, autoCompleteTag2;
    private CheckBox checkboxTwoTags;
    private View secondTagSection;
    private RadioGroup radioLogic;
    private Button btnSearch;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dataManager = DataManager.getInstance(this);

        spinnerTagType1 = findViewById(R.id.spinner_tag_type1);
        spinnerTagType2 = findViewById(R.id.spinner_tag_type2);
        autoCompleteTag1 = findViewById(R.id.auto_complete_tag1);
        autoCompleteTag2 = findViewById(R.id.auto_complete_tag2);
        checkboxTwoTags = findViewById(R.id.checkbox_two_tags);
        secondTagSection = findViewById(R.id.second_tag_section);
        radioLogic = findViewById(R.id.radio_logic);
        btnSearch = findViewById(R.id.btn_search);
        recyclerView = findViewById(R.id.recycler_results);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search Photos");
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        checkboxTwoTags.setOnCheckedChangeListener((buttonView, isChecked) -> {
            secondTagSection.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            radioLogic.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        spinnerTagType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateAutoComplete1();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTagType2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateAutoComplete2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSearch.setOnClickListener(v -> performSearch());

        updateAutoComplete1();
        updateAutoComplete2();
    }

    private void updateAutoComplete1() {
        String type = spinnerTagType1.getSelectedItem().toString();
        List<String> values = dataManager.getAllTagValues(type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, values);
        autoCompleteTag1.setAdapter(adapter);
    }

    private void updateAutoComplete2() {
        String type = spinnerTagType2.getSelectedItem().toString();
        List<String> values = dataManager.getAllTagValues(type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, values);
        autoCompleteTag2.setAdapter(adapter);
    }

    private void performSearch() {
        String type1 = spinnerTagType1.getSelectedItem().toString();
        String value1 = autoCompleteTag1.getText().toString().trim();

        if (value1.isEmpty()) {
            Toast.makeText(this, "Please enter a value for Tag 1", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Photo> results;
        if (checkboxTwoTags.isChecked()) {
            String type2 = spinnerTagType2.getSelectedItem().toString();
            String value2 = autoCompleteTag2.getText().toString().trim();
            boolean isAnd = radioLogic.getCheckedRadioButtonId() == R.id.radio_and;
            
            if (value2.isEmpty()) {
                Toast.makeText(this, "Please enter a value for Tag 2", Toast.LENGTH_SHORT).show();
                return;
            }
            results = dataManager.searchPhotos(type1, value1, type2, value2, isAnd);
        } else {
            results = dataManager.searchPhotos(type1, value1, null, null, false);
        }

        displayResults(results);
    }

    private void displayResults(List<Photo> results) {
        if (results.isEmpty()) {
            Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new PhotoAdapter(this, results, this);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(results);
            }
        }
    }

    @Override
    public void onPhotoClick(Photo photo, int position) {
        String albumName = findAlbumContainingPhoto(photo);
        if (albumName != null) {
            Album album = dataManager.getAlbum(albumName);
            int indexInAlbum = album.getPhotos().indexOf(photo);
            
            Intent intent = new Intent(this, PhotoDisplayActivity.class);
            intent.putExtra("ALBUM_NAME", albumName);
            intent.putExtra("PHOTO_POSITION", indexInAlbum);
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteClick(Photo photo) {
        Toast.makeText(this, "Deletion not available from search results", Toast.LENGTH_SHORT).show();
    }

    private String findAlbumContainingPhoto(Photo photo) {
        for (Album album : dataManager.getAllAlbums()) {
            if (album.getPhotos().contains(photo)) {
                return album.getName();
            }
        }
        return null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
