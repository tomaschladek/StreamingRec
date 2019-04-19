package testing.streamingrec.algorithms.samplers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.samplers.DynamicReservoirSampler;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DynamicReservoirSamplerTest {

    private DynamicReservoirSampler sampler;
    private ArrayList<Long> collection;

    @BeforeEach
    void setUp() {
        this.sampler = new DynamicReservoirSampler(1);
        this.collection = new ArrayList<>();
    }

    @Test
    void testSampling() {
        sampler.add(collection,1L);
        assertEquals(1,collection.size());
        assertEquals(1,collection.get(0));
        for (long index = 2; index < 102; index++)
            sampler.add(collection,index);
        assertTrue(collection.get(0) != 1L);
        assertEquals(1,collection.size());
    }
}