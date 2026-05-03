package com.example.photos_cs213;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.photos_cs213.activities.AlbumDetailActivity;
import com.example.photos_cs213.activities.SearchActivity;
import com.example.photos_cs213.model.Album;
import com.example.photos_cs213.model.DataManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.OnAlbumClickListener {

    private DataManager dataManager;
    private AlbumAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyState;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataManager = DataManager.getInstance(this);

        recyclerView = findViewById(R.id.recycler_albums);
        emptyState = findViewById(R.id.empty_state);
        fabAdd = findViewById(R.id.fab_add_album);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        refreshData();

        fabAdd.setOnClickListener(v -> showAddAlbumDialog());
    }

    private void refreshData() {
        List<Album> albums = dataManager.getAllAlbums();
        if (adapter == null) {
            adapter = new AlbumAdapter(this, albums, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(albums);
        }

        if (albums.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showAddAlbumDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_input, null);
        EditText input = view.findViewById(R.id.edit_text_input);
        input.setHint("Album Name");

        new AlertDialog.Builder(this)
                .setTitle("Add Album")
                .setMessage("Enter album name:")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (dataManager.albumExists(name)) {
                        Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        dataManager.addAlbum(new Album(name));
                        refreshData();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onAlbumClick(Album album) {
        Intent intent = new Intent(this, AlbumDetailActivity.class);
        intent.putExtra("ALBUM_NAME", album.getName());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Album album) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Album")
                .setMessage("Are you sure you want to delete '" + album.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dataManager.removeAlbum(album);
                    refreshData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onRenameClick(Album album) {
        View view = getLayoutInflater().inflate(R.layout.dialog_input, null);
        EditText input = view.findViewById(R.id.edit_text_input);
        input.setText(album.getName());

        new AlertDialog.Builder(this)
                .setTitle("Rename Album")
                .setMessage("Enter new name for '" + album.getName() + "':")
                .setView(view)
                .setPositiveButton("Rename", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (newName.isEmpty()) {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (newName.equalsIgnoreCase(album.getName())) {
                        // Same name, do nothing
                    } else if (dataManager.albumExists(newName)) {
                        Toast.makeText(this, "Album name already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        dataManager.renameAlbum(album, newName);
                        refreshData();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
