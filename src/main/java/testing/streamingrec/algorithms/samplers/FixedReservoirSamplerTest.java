package testing.streamingrec.algorithms.samplers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.samplers.FixedReservoirSampler;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixedReservoirSamplerTest {
    private FixedReservoirSampler sampler;
    private ArrayList<Long> collection;

    @BeforeEach
    void setUp() {
        this.collection = new ArrayList<>();
    }

    @Test
    void testAlwaysSampling() {
        this.sampler = new FixedReservoirSampler(1,0);
        sampler.add(collection,1L);
        assertEquals(1,collection.size());
        assertEquals(1,collection.get(0));
        sampler.add(collection,2L);
        assertEquals(2L, collection.get(0));
        assertEquals(1,collection.size());
    }

    @Test
    void testNeverSampling() {
        this.sampler = new FixedReservoirSampler(1,Integer.MAX_VALUE-1);
        sampler.add(collection,1L);
        assertEquals(1,collection.size());
        assertEquals(1,collection.get(0));
        sampler.add(collection,2L);
        assertEquals(1L, collection.get(0));
        assertEquals(1,collection.size());
    }
}