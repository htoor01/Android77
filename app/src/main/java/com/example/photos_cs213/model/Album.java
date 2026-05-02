package com.example.photos_cs213.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Haaris Toor
 * @author Aditi Negi
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos;

    /**
     * Creates a new Album with the specified name.
     *
     * @param name the album name
     * @throws IllegalArgumentException if name is null or empty
     */
    public Album(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Album name cannot be null or empty");
        }
        this.name = name.trim();
        this.photos = new ArrayList<>();
    }

    /**
     * Gets the album name.
     *
     * @return the album name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the album name.
     *
     * @param name the new album name
     * @throws IllegalArgumentException if name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Album name cannot be null or empty");
        }
        this.name = name.trim();
    }

    /**
     * Gets the list of photos in this album.
     *
     * @return a copy of the photos list
     */
    public List<Photo> getPhotos() {
        return new ArrayList<>(photos);
    }

    /**
     * Adds a photo to this album.
     *
     * @param photo the photo to add
     * @return true if added, false if photo already exists
     */
    public boolean addPhoto(Photo photo) {
        if (photo == null || photos.contains(photo)) {
            return false;
        }
        return photos.add(photo);
    }

    /**
     * Removes a photo from this album.
     *
     * @param photo the photo to remove
     * @return true if removed, false if not found
     */
    public boolean removePhoto(Photo photo) {
        return photos.remove(photo);
    }

    /**
     * Gets the number of photos in this album.
     *
     * @return the photo count
     */
    public int getPhotoCount() {
        return photos.size();
    }

    /**
     * Checks if the album contains a specific photo.
     *
     * @param photo the photo to check
     * @return true if photo exists in album
     */
    public boolean containsPhoto(Photo photo) {
        return photos.contains(photo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Album album = (Album) obj;
        return name.equalsIgnoreCase(album.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return name + " (" + photos.size() + " photos)";
    }
}

