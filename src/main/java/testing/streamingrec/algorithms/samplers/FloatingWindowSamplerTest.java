package testing.streamingrec.algorithms.samplers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.samplers.FloatingWindowSampler;
import tudo.streamingrec.algorithms.samplers.ISampler;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FloatingWindowSamplerTest {

    private ArrayList<Long> collection;

    @BeforeEach
    void setUp() {
        this.collection = new ArrayList<>();
    }

    @Test
    void addSize1() {
        ISampler sampler = new FloatingWindowSampler(1);
        sampler.add(collection,1l);
        assertEquals(1,collection.size());
        assertEquals(1,collection.get(0));
        sampler.add(collection,2l);
        assertEquals(1,collection.size());
        assertEquals(2,collection.get(0));
    }

    @Test
    void addSize2() {
        ISampler sampler = new FloatingWindowSampler(2);
        sampler.add(collection,1l);
        assertEquals(1,collection.size());
        assertEquals(1,collection.get(0));
        sampler.add(collection,2l);
        assertEquals(2,collection.size());
        assertEquals(2,collection.get(0));
        assertEquals(1,collection.get(1));
        sampler.add(collection,3l);
        assertEquals(2,collection.size());
        assertEquals(3,collection.get(0));
        assertEquals(2,collection.get(1));
    }
}