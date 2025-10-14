package uk.ac.ncl.csc8404.pcretailer.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Immutable implementation of a preset computer model backed by a manufacturer.
 */
public final class PresetModel implements PresetComputerModel {

    //  preset stuff should stay same, feels safer that way

    private final String manufacturer;
    private final String name;
    private final SortedSet<String> parts;

    private PresetModel(String manufacturer, String name, SortedSet<String> parts) {
        this.manufacturer = manufacturer;
        this.name = name;
        this.parts = parts;
    }

    /**
     * Factory for creating preset models, ensuring validation and immutability.
     *
     * @param manufacturer the manufacturer; must be non-blank.
     * @param name         the model name; must be non-blank.
     * @param parts        the collection of parts; each must be a non-blank string.
     * @return a new immutable {@link PresetModel}.
     */
    public static PresetModel of(String manufacturer, String name, Collection<String> parts) {
        String safeManufacturer = requireText("manufacturer", manufacturer);
        String safeName = requireText("name", name);
        SortedSet<String> safeParts = validateParts(parts);
        return new PresetModel(safeManufacturer, safeName, safeParts);
    }

    @Override
    public String getManufacturer() {
        return manufacturer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getParts() {
        return Collections.unmodifiableSet(parts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PresetModel that)) {
            return false;
        }
        return manufacturer.equals(that.manufacturer)
                && name.equals(that.name)
                && parts.equals(that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manufacturer, name, parts);
    }

    @Override
    public String toString() {
        return "PresetModel{" +
                "manufacturer='" + manufacturer + '\'' +
                ", name='" + name + '\'' +
                ", parts=" + parts +
                '}';
    }

    private static String requireText(String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must be non-blank");
        }
        return value.trim();
    }

    private static SortedSet<String> validateParts(Collection<String> parts) {
        if (parts == null || parts.isEmpty()) {
            throw new IllegalArgumentException("parts must be supplied");
        }
        SortedSet<String> sanitized = new TreeSet<>();
        for (String part : parts) {
            String safe = requireText("part", part);
            sanitized.add(safe);
        }
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("parts must not be blank");
        }
        return Collections.unmodifiableSortedSet(sanitized);
    }
}
