package com.example.photos_cs213.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a photo with its URI and tags.
 *
 * @author Haaris Toor
 * @author Aditi Negi
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * String representation of the photo's content URI.
     * Stores the persistent URI granted by the system.
     */
    private String uriString;

    /**
     * The list of tags associated with this photo.
     * A photo can have multiple tags with the same name but different values.
     */
    private List<Tag> tags;

    /**
     * Creates a new Photo with the specified URI.
     *
     * @param uriString the content URI string of the photo
     * @throws IllegalArgumentException if uriString is null or empty
     */
    public Photo(String uriString) {
        if (uriString == null || uriString.trim().isEmpty()) {
            throw new IllegalArgumentException("URI string cannot be null or empty");
        }
        this.uriString = uriString;
        this.tags = new ArrayList<>();
    }

    /**
     * Gets the URI string of this photo.
     *
     * @return the URI string
     */
    public String getUriString() {
        return uriString;
    }

    /**
     * Gets the display name for this photo (filename from URI).
     *
     * @return the filename or URI string if extraction fails
     */
    public String getDisplayName() {
        // Extract filename from URI (last segment after /)
        String[] parts = uriString.split("/");
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1];
            // Remove any query parameters
            int queryIndex = lastPart.indexOf('?');
            if (queryIndex > 0) {
                return lastPart.substring(0, queryIndex);
            }
            return lastPart;
        }
        return uriString;
    }

    /**
     * Gets the list of tags associated with this photo.
     *
     * @return a copy of the tags list
     */
    public List<Tag> getTags() {
        return new ArrayList<>(tags);
    }

    /**
     * Adds a tag to this photo if it doesn't already exist.
     * A photo cannot have duplicate tags (same name and value combination).
     * However, a photo can have multiple tags with the same name but different values.
     *
     * @param tag the tag to add
     * @return true if the tag was added, false if it already exists
     * @throws IllegalArgumentException if tag is null
     */
    public boolean addTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null");
        }
        if (tags.contains(tag)) {
            return false;
        }
        return tags.add(tag);
    }

    /**
     * Removes a tag from this photo.
     *
     * @param tag the tag to remove
     * @return true if the tag was removed, false if it didn't exist
     */
    public boolean removeTag(Tag tag) {
        return tags.remove(tag);
    }

    /**
     * Checks if this photo has a specific tag.
     *
     * @param tag the tag to check for
     * @return true if the photo has this tag
     */
    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    /**
     * Gets all tags with a specific name.
     *
     * @param tagName the name of the tag to search for
     * @return a list of tags with the specified name
     */
    public List<Tag> getTagsByName(String tagName) {
        List<Tag> result = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getName().equalsIgnoreCase(tagName)) {
                result.add(tag);
            }
        }
        return result;
    }

    /**
     * Checks if this photo has a tag with the specified name and value.
     * Case-insensitive comparison.
     *
     * @param tagName the tag name
     * @param tagValue the tag value
     * @return true if the photo has such a tag
     */
    public boolean hasTag(String tagName, String tagValue) {
        for (Tag tag : tags) {
            if (tag.getName().equalsIgnoreCase(tagName) &&
                    tag.getValue().equalsIgnoreCase(tagValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this photo has a tag whose value starts with the given prefix.
     * Used for auto-completion in search. Case-insensitive.
     *
     * @param tagName the tag name to match
     * @param valuePrefix the prefix to match against tag values
     * @return true if any tag matches
     */
    public boolean hasTagStartingWith(String tagName, String valuePrefix) {
        for (Tag tag : tags) {
            if (tag.getName().equalsIgnoreCase(tagName) &&
                    tag.getValue().toLowerCase().startsWith(valuePrefix.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Photo photo = (Photo) obj;
        return uriString.equals(photo.uriString);
    }

    @Override
    public int hashCode() {
        return uriString.hashCode();
    }

    @Override
    public String toString() {
        return getDisplayName() + " (" + tags.size() + " tags)";
    }
}
