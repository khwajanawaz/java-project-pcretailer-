package uk.ac.ncl.csc8404.pcretailer.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PresetModelTest {

    // just checking preset stays solid, hope thats right

    @Test
    void createsImmutablePresetModel() {
        PresetModel model = PresetModel.of("ACME", "Workstation", List.of("CPU", "RAM", "SSD"));
        assertEquals("ACME", model.getManufacturer());
        assertEquals("Workstation", model.getName());
        assertEquals(Set.of("CPU", "RAM", "SSD"), model.getParts());
        assertThrows(UnsupportedOperationException.class, () -> model.getParts().add("GPU"));
        assertTrue(model.toString().contains("ACME"));
    }

    @Test
    void rejectsBlankFields() {
        assertThrows(IllegalArgumentException.class,
                () -> PresetModel.of(" ", "Workstation", List.of("CPU")));
        assertThrows(IllegalArgumentException.class,
                () -> PresetModel.of("ACME", "", List.of("CPU")));
        assertThrows(IllegalArgumentException.class,
                () -> PresetModel.of("ACME", "Workstation", List.of(" ")));
    }
}
