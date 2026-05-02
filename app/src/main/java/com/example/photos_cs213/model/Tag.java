package com.example.photos_cs213.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a tag with a name and value pair for a photo.
 * For Android app, only "person" and "location" tag types are allowed.
 *
 * <p><b>Tag Multiplicity:</b> Each Tag instance represents a single name-value pair.
 * However, a photo can have multiple Tag instances with the same name but different values.
 * This allows for multi-valued tags. For example, a photo can have:
 * <ul>
 *   <li>Tag("person", "John")</li>
 *   <li>Tag("person", "Jane")</li>
 *   <li>Tag("location", "New Brunswick")</li>
 * </ul>
 *
 * @author Haaris Toor
 * @author Aditi Negi
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_PERSON = "person";
    public static final String TYPE_LOCATION = "location";

    private String name;
    private String value;

    /**
     * Creates a new Tag with the specified name and value.
     * Only "person" and "location" are valid tag names.
     *
     * @param name the tag name/type (must be "person" or "location")
     * @param value the tag value
     * @throws IllegalArgumentException if name is not "person" or "location",
     *         or if name/value is null or empty
     */
    public Tag(String name, String value) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag value cannot be null or empty");
        }

        String trimmedName = name.trim().toLowerCase();
        if (!trimmedName.equals(TYPE_PERSON) && !trimmedName.equals(TYPE_LOCATION)) {
            throw new IllegalArgumentException("Tag name must be 'person' or 'location'");
        }

        this.name = trimmedName;
        this.value = value.trim();
    }

    /**
     * Gets the name/type of this tag.
     *
     * @return the tag name ("person" or "location")
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of this tag.
     *
     * @return the tag value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value the new tag value
     * @throws IllegalArgumentException if value is null or empty
     */
    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag value cannot be null or empty");
        }
        this.value = value.trim();
    }

    /**
     * Checks if this tag's value starts with the given prefix.
     * Used for auto-completion. Case-insensitive.
     *
     * @param prefix the prefix to check
     * @return true if value starts with prefix
     */
    public boolean valueStartsWith(String prefix) {
        if (prefix == null) return false;
        return value.toLowerCase().startsWith(prefix.toLowerCase());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        // Case-insensitive comparison for both name and value
        return name.equalsIgnoreCase(tag.name) && value.equalsIgnoreCase(tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), value.toLowerCase());
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
