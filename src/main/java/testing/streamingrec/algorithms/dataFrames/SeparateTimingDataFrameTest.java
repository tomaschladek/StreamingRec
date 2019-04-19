package testing.streamingrec.algorithms.dataFrames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.dataFrames.SeparateTimingDataFrame;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeparateTimingDataFrameTest extends AbstractDataFrameTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        this.dataFrame = new SeparateTimingDataFrame(new int[]{10,20,30});
    }

    @Test
    void getTrainingData() {
        assertEquals(1, dataFrame.getTrainingData(timestamp).size());
    }

    @Test
    void getTestingData() {
        List<Long> testingData = dataFrame.getTestingData();
        assertEquals(0, testingData.size());
        for (List<Long> sets : dataFrame.getTrainingData(timestamp))
        {
            sets.add(1l);
        }
        assertEquals(0, dataFrame.getTestingData().size());
    }

    @Test
    void update() {
        generateTransactions();
        assertEquals(10, dataFrame.getTestingData().size());
        assertEquals(4, dataFrame.getTrainingData(timestamp).get(0).size());
    }
}