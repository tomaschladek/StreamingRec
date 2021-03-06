package testing.streamingrec.algorithms.samplers;

import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.samplers.WindowSampler;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class WindowSamplerTest {

    @Test
    void add() {
        WindowSampler sampler = new WindowSampler();
        ArrayList<Long> collection = new ArrayList<>();
        assertNull(sampler.add(collection,1L));
        assertEquals(1,collection.size());
        assertEquals(1,collection.get(0));
        assertNull(sampler.add(collection,2L));
        assertEquals(2,collection.size());
        assertEquals(1,collection.get(0));
        assertEquals(2,collection.get(1));
    }
}