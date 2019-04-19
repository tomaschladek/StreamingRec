package testing.streamingrec.algorithms.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.dtos.FrameTimeDto;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FrameTimeDtoTest {

    private FrameTimeDto frame;
    private FrameTimeDto otherFrame;

    @BeforeEach
    void setUp() {
        this.frame = new FrameTimeDto();
        this.otherFrame = new FrameTimeDto();
        frame.collection.add(1L);
        frame.timestampThreshold = new Date(1,0,0,0,0,0);
        otherFrame.collection.add(2L);
        otherFrame.timestampThreshold = new Date(2,0,0,0,0,0);
    }

    @Test
    void assignAndClear() {
        frame.assignAndClear(otherFrame);
        assertEquals(1, frame.collection.size());
        assertEquals(2, frame.collection.get(0));
        assertEquals(new Date(1,0,0,0,0,0), frame.timestampThreshold);

        assertEquals(new Date(2,0,0,0,0,0), otherFrame.timestampThreshold);
        assertEquals(0, otherFrame.collection.size());
    }
}