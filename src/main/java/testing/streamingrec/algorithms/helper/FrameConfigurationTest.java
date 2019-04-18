package testing.streamingrec.algorithms.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.dtos.FrameConfiguration;

import static org.junit.jupiter.api.Assertions.*;

class FrameConfigurationTest {

    private FrameConfiguration configuration;

    @BeforeEach
    void setUp() {
        this.configuration = new FrameConfiguration(new int[]{10,20,30});
    }

    @Test
    void getNext() {
        assertEquals(10,configuration.getNext());
        assertEquals(20,configuration.getNext());
        assertEquals(30,configuration.getNext());
        assertEquals(10,configuration.getNext());
    }
}