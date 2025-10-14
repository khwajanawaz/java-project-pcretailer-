package uk.ac.ncl.csc8404.pcretailer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomModelFactoryTest {

    // make sure factory dont allow same names twice, hehe

    private CustomModelFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CustomModelFactory();
    }

    @Test
    void enforcesUniqueNamesCaseInsensitive() {
        CustomModel first = factory.create("BuilderOne", List.of("CPU", "RAM"));
        assertEquals("BuilderOne", first.getName());
        assertThrows(IllegalStateException.class, () -> factory.create("builderone", List.of("CPU", "SSD")));
    }

    @Test
    void supportsPartModificationViaNewInstances() {
        CustomModel model = factory.create("Rig", List.of("CPU", "RAM"));
        CustomComputerModel withGpu = model.withPart("GPU");
        assertNotSame(model, withGpu);
        assertTrue(withGpu.getParts().contains("GPU"));
        assertFalse(model.getParts().contains("GPU"));
        CustomComputerModel withoutRam = withGpu.withoutPart("RAM");
        assertFalse(withoutRam.getParts().contains("RAM"));
    }

    @Test
    void refusesEmptyConfiguration() {
        CustomModel model = factory.create("Laptop", List.of("CPU"));
        assertThrows(IllegalStateException.class, () -> model.withoutPart("CPU"));
    }
}
