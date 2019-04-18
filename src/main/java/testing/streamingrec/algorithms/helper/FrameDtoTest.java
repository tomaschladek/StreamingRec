package testing.streamingrec.algorithms.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.dtos.FrameDto;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FrameDtoTest {

    private FrameDto frame;
    private FrameDto otherFrame;

    @BeforeEach
    void setUp() {
        this.frame = new FrameDto(5);
        this.otherFrame = new FrameDto(5);
        frame.collection.add(1l);
        frame.timestampThreshold = new Date(1,0,0,0,0,0);
        otherFrame.collection.add(2l);
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