package uk.ac.ncl.csc8404.pcretailer.model;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Factory responsible for creating {@link CustomModel} instances while enforcing name uniqueness.
 */
public final class CustomModelFactory {

    // dont let same name twice coz that gets confuzing

    private final Set<String> allocatedNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Creates a new custom model while ensuring that the name has not been used previously within this factory.
     *
     * @param name  proposed unique name.
     * @param parts initial parts.
     * @return immutable custom model.
     * @throws IllegalStateException    if the name was already allocated.
     * @throws IllegalArgumentException if validation fails.
     */
    public CustomModel create(String name, Collection<String> parts) {
        String safeName = requireText("name", name);
        if (allocatedNames.contains(safeName)) {
            throw new IllegalStateException("custom model name already exists: " + name);
        }
        CustomModel model = CustomModel.of(safeName, parts);
        allocatedNames.add(model.getName());
        return model;
    }

    /**
     * Releases a name from the allocation pool, primarily intended for testing scenarios.
     *
     * @param model the custom model whose name should be released.
     */
    public void release(CustomComputerModel model) {
        if (model != null) {
            allocatedNames.remove(model.getName().trim());
        }
    }

    private static String requireText(String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must be non-blank");
        }
        return value.trim();
    }
}
