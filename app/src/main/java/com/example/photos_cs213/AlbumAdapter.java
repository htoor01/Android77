package com.example.photos_cs213;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos_cs213.model.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context context;
    private List<Album> albums;
    private OnAlbumClickListener listener;

    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
        void onDeleteClick(Album album);
        void onRenameClick(Album album);
    }

    public AlbumAdapter(Context context, List<Album> albums, OnAlbumClickListener listener) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.albumName.setText(album.getName());
        holder.photoCount.setText(album.getPhotoCount() + " photos");

        holder.itemView.setOnClickListener(v -> listener.onAlbumClick(album));
        holder.btnRename.setOnClickListener(v -> listener.onRenameClick(album));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(album));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void updateData(List<Album> newAlbums) {
        this.albums = newAlbums;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView albumName;
        TextView photoCount;
        ImageButton btnRename;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.album_name);
            photoCount = itemView.findViewById(R.id.photo_count);
            btnRename = itemView.findViewById(R.id.btn_rename);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
