package testing.streamingrec.algorithms.dataFrames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tudo.streamingrec.algorithms.dataFrames.SeparateTimingDataFrame;
import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SeparateTimingDataFrameTest extends AbstractDataFrameTest {

    @BeforeEach
    public void setUp() {
        this.dataFrame = new SeparateTimingDataFrame(new int[]{10*60,20*60,30*60});
        super.setUp();
    }

    @Test
    void getTrainingData() {
        assertEquals(1, dataFrame.getTrainingData(timestamp).size());
    }

    @Test
    void getTestingData() {
        IStreamingExecutor testingData = dataFrame.getTestingData();
        assertEquals(0, testingData.getCollection().size());
        for (IStreamingExecutor executor : dataFrame.getTrainingData(timestamp))
        {
            executor.getCollection().add(1L);
        }
        assertEquals(0, dataFrame.getTestingData().getCollection().size());
    }

    @Test
    void update() {
        generateTransactions();
        assertEquals(9, dataFrame.getTestingData().getCollection().size());
        assertEquals(5, dataFrame.getTrainingData(timestamp).get(0).getCollection().size());
    }
}