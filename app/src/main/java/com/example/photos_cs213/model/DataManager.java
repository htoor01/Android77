package com.example.photos_cs213.model;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages data persistence for the Android Photos app.
 * Handles saving and loading albums using JSON serialization.
 *
 * @author Haaris Toor
 * @author Aditi Negi
 */
public class DataManager {
    private static final String ALBUMS_FILE = "albums.json";
    private static DataManager instance;

    private Context context;
    private List<Album> albums;
    private Gson gson;

    /**
     * Private constructor for Singleton pattern.
     *
     * @param context the application context
     */
    private DataManager(Context context) {
        this.context = context.getApplicationContext();
        this.albums = new ArrayList<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Gets the singleton instance of DataManager.
     *
     * @param context the application context
     * @return the DataManager instance
     */
    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
            instance.loadAlbums();
        }
        return instance;
    }

    /**
     * Gets all albums.
     *
     * @return a copy of the albums list
     */
    public List<Album> getAllAlbums() {
        return new ArrayList<>(albums);
    }

    /**
     * Adds a new album.
     *
     * @param album the album to add
     * @return true if added, false if album with same name exists
     */
    public boolean addAlbum(Album album) {
        if (album == null || albumExists(album.getName())) {
            return false;
        }
        albums.add(album);
        saveAlbums();
        return true;
    }

    /**
     * Removes an album.
     *
     * @param album the album to remove
     * @return true if removed
     */
    public boolean removeAlbum(Album album) {
        boolean removed = albums.remove(album);
        if (removed) {
            saveAlbums();
        }
        return removed;
    }

    /**
     * Renames an album.
     *
     * @param album the album to rename
     * @param newName the new name
     * @return true if renamed, false if new name already exists
     */
    public boolean renameAlbum(Album album, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }
        if (!album.getName().equalsIgnoreCase(newName) && albumExists(newName)) {
            return false;
        }
        album.setName(newName);
        saveAlbums();
        return true;
    }

    /**
     * Gets an album by name.
     *
     * @param name the album name (case-insensitive)
     * @return the album, or null if not found
     */
    public Album getAlbum(String name) {
        for (Album album : albums) {
            if (album.getName().equalsIgnoreCase(name)) {
                return album;
            }
        }
        return null;
    }

    /**
     * Checks if an album with the given name exists.
     *
     * @param name the album name (case-insensitive)
     * @return true if exists
     */
    public boolean albumExists(String name) {
        return getAlbum(name) != null;
    }

    /**
     * Gets all unique tag values for a given tag name across all photos.
     * Used for auto-completion in search.
     *
     * @param tagName the tag name ("person" or "location")
     * @return list of unique tag values
     */
    public List<String> getAllTagValues(String tagName) {
        List<String> values = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                for (Tag tag : photo.getTags()) {
                    if (tag.getName().equalsIgnoreCase(tagName)) {
                        String value = tag.getValue();
                        // Add unique values only (case-insensitive)
                        boolean exists = false;
                        for (String existing : values) {
                            if (existing.equalsIgnoreCase(value)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            values.add(value);
                        }
                    }
                }
            }
        }
        return values;
    }

    /**
     * Searches for photos matching tag criteria.
     *
     * @param tagName1 first tag name
     * @param tagValue1 first tag value
     * @param tagName2 second tag name (null if single tag search)
     * @param tagValue2 second tag value (null if single tag search)
     * @param useConjunction true for AND, false for OR (ignored for single tag)
     * @return list of matching photos
     */
    public List<Photo> searchPhotos(String tagName1, String tagValue1,
                                    String tagName2, String tagValue2,
                                    boolean useConjunction) {
        List<Photo> results = new ArrayList<>();

        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                boolean match1 = photo.hasTagStartingWith(tagName1, tagValue1);

                if (tagName2 == null || tagValue2 == null) {
                    // Single tag search
                    if (match1 && !results.contains(photo)) {
                        results.add(photo);
                    }
                } else {
                    // Two tag search
                    boolean match2 = photo.hasTagStartingWith(tagName2, tagValue2);

                    if (useConjunction) {
                        // AND: both must match
                        if (match1 && match2 && !results.contains(photo)) {
                            results.add(photo);
                        }
                    } else {
                        // OR: at least one must match
                        if ((match1 || match2) && !results.contains(photo)) {
                            results.add(photo);
                        }
                    }
                }
            }
        }

        return results;
    }

    /**
     * Moves a photo from one album to another.
     *
     * @param photo the photo to move
     * @param fromAlbum source album
     * @param toAlbum destination album
     * @return true if moved successfully
     */
    public boolean movePhoto(Photo photo, Album fromAlbum, Album toAlbum) {
        if (photo == null || fromAlbum == null || toAlbum == null) {
            return false;
        }
        if (!fromAlbum.containsPhoto(photo)) {
            return false;
        }

        fromAlbum.removePhoto(photo);
        toAlbum.addPhoto(photo);
        saveAlbums();
        return true;
    }

    /**
     * Saves all albums to storage.
     * Uses JSON serialization with Gson.
     */
    public void saveAlbums() {
        try {
            File file = new File(context.getFilesDir(), ALBUMS_FILE);
            FileWriter writer = new FileWriter(file);
            gson.toJson(albums, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads albums from storage.
     * Uses JSON deserialization with Gson.
     */
    private void loadAlbums() {
        try {
            File file = new File(context.getFilesDir(), ALBUMS_FILE);
            if (!file.exists()) {
                albums = new ArrayList<>();
                return;
            }

            FileReader reader = new FileReader(file);
            Type listType = new TypeToken<ArrayList<Album>>(){}.getType();
            albums = gson.fromJson(reader, listType);
            reader.close();

            if (albums == null) {
                albums = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            albums = new ArrayList<>();
        }
    }

    /**
     * Clears all data (for testing purposes).
     */
    public void clearAllData() {
        albums.clear();
        saveAlbums();
    }
}
