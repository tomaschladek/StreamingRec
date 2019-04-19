package testing.streamingrec.algorithms.dataFrames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.dataFrames.OverlappingTimingDataFrame;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class OverlappingTimingDataFrameTest extends AbstractDataFrameTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        dataFrame = new OverlappingTimingDataFrame(new int[]{10,20,30},5);
    }

    @Test
    void getTrainingData() {
        List<List<Long>> trainingData = dataFrame.getTrainingData(timestamp);
        assertEquals(2, trainingData.size());
        assertNotSame(trainingData.get(0),trainingData.get(1));
    }

    @Test
    void getTestingData() {
        List<Long> testingData = dataFrame.getTestingData();
        assertEquals(0, testingData.size());
        for (List<Long> sets : dataFrame.getTrainingData(timestamp))
        {
            sets.add(1l);
        }
        assertEquals(1, testingData.size());
        assertEquals(1l, testingData.get(0));
    }

    /**
     *
     *  Frames: 1|10|20
     *  Training+Testing=> 1+1 => 5+11 => 4+9
     *  Training didnt start in the end. Training = testing.
     **/
    @Test
    void update() {
        generateTransactions();
        assertEquals(9, dataFrame.getTestingData().size());
        assertEquals(9, dataFrame.getTrainingData(timestamp).get(0).size());
    }
}