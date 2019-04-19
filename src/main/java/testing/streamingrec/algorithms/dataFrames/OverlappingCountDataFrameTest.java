package testing.streamingrec.algorithms.dataFrames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.dataFrames.OverlappingCountDataFrame;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OverlappingCountDataFrameTest extends AbstractDataFrameTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        dataFrame = new OverlappingCountDataFrame(new int[]{10,20,30},5);
    }

    @Test
    void getTrainingData() {
        List<List<Long>> trainingData = dataFrame.getTrainingData(timestamp);
        assertEquals(1, trainingData.size());
        assertEquals(0, trainingData.get(0).size());
    }

    @Test
    void getTestingData() {
        List<Long> testingData = dataFrame.getTestingData();
        assertEquals(0, testingData.size());
        for (List<Long> sets : dataFrame.getTrainingData(timestamp))
        {
            sets.add(1L);
        }
        assertEquals(1, dataFrame.getTestingData().size());
        assertEquals(1L, dataFrame.getTestingData().get(0));
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
        assertEquals(10, dataFrame.getTestingData().size());
        assertEquals(10, dataFrame.getTrainingData(timestamp).get(0).size());
    }
}