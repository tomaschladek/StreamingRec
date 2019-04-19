package testing.streamingrec.algorithms.dataFrames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.dataFrames.SingleDataFrame;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SingleDataFrameTest extends AbstractDataFrameTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        this.dataFrame = new SingleDataFrame();
    }

    @Test
    void getTrainingData() {
        assertEquals(1,dataFrame.getTrainingData(timestamp).size());
    }

    @Test
    void getTestingData() {
        List<Long> testingData = dataFrame.getTestingData();
        assertEquals(0, testingData.size());
        for (List<Long> sets : dataFrame.getTrainingData(timestamp))
        {
            sets.add(1l);
        }
        assertEquals(1, dataFrame.getTestingData().size());
        assertEquals(1l, dataFrame.getTestingData().get(0));
    }

    @Test
    void update() {
        generateTransactions();
        assertEquals(15, dataFrame.getTestingData().size());
        assertEquals(15, dataFrame.getTrainingData(timestamp).get(0).size());

    }
}