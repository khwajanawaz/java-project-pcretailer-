package uk.ac.ncl.csc8404.pcretailer.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Immutable implementation of a custom computer model.
 */
public final class CustomModel implements CustomComputerModel {

    //  comment: we can swap parts but keep the rules nice an tidy

    private final String name;
    private final SortedSet<String> parts;

    CustomModel(String name, SortedSet<String> parts) {
        this.name = name;
        this.parts = parts;
    }

    /**
     * Creates a new custom model with validated parts.
     *
     * @param name  unique name of the model.
     * @param parts initial parts collection.
     * @return new custom model instance.
     */
    public static CustomModel of(String name, Collection<String> parts) {
        String safeName = requireText("name", name);
        SortedSet<String> safeParts = sanitizeParts(parts);
        return new CustomModel(safeName, safeParts);
    }

    @Override
    public CustomComputerModel withPart(String part) {
        String safe = requireText("part", part);
        if (parts.contains(safe)) {
            return this;
        }
        SortedSet<String> updated = new TreeSet<>(parts);
        updated.add(safe);
        return new CustomModel(name, Collections.unmodifiableSortedSet(updated));
    }

    @Override
    public CustomComputerModel withoutPart(String part) {
        String safe = requireText("part", part);
        if (!parts.contains(safe)) {
            return this;
        }
        SortedSet<String> updated = new TreeSet<>(parts);
        updated.remove(safe);
        if (updated.isEmpty()) {
            throw new IllegalStateException("custom model must contain at least one part");
        }
        return new CustomModel(name, Collections.unmodifiableSortedSet(updated));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SortedSet<String> getParts() {
        return Collections.unmodifiableSortedSet(parts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomModel that)) {
            return false;
        }
        return name.equals(that.name) && parts.equals(that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parts);
    }

    @Override
    public String toString() {
        return "CustomModel{" +
                "name='" + name + '\'' +
                ", parts=" + parts +
                '}';
    }

    private static SortedSet<String> sanitizeParts(Collection<String> parts) {
        SortedSet<String> sanitized = new TreeSet<>();
        if (parts != null) {
            for (String part : parts) {
                sanitized.add(requireText("part", part));
            }
        }
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("custom model must contain at least one part");
        }
        return Collections.unmodifiableSortedSet(sanitized);
    }

    private static String requireText(String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must be non-blank");
        }
        return value.trim();
    }
}
