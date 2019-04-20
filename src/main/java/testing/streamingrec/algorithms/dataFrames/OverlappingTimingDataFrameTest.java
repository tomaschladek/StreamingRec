package testing.streamingrec.algorithms.dataFrames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tudo.streamingrec.algorithms.dataFrames.OverlappingTimingDataFrame;
import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OverlappingTimingDataFrameTest extends AbstractDataFrameTest {

    @BeforeEach
    public void setUp() {
        dataFrame = new OverlappingTimingDataFrame(new int[]{10,20,30},5*60);
        super.setUp();
    }

    @Test
    void getTrainingData() {
        List<IStreamingExecutor> trainingData = dataFrame.getTrainingData(timestamp);
        assertEquals(2, trainingData.size());
        assertNotSame(trainingData.get(0),trainingData.get(1));
    }

    @Test
    void getTestingData() {
        IStreamingExecutor testingData = dataFrame.getTestingData();
        assertEquals(0, testingData.getCollection().size());
        for (IStreamingExecutor sets : dataFrame.getTrainingData(timestamp))
        {
            sets.getCollection().add(1L);
        }
        assertEquals(1, dataFrame.getTestingData().getCollection().size());
        assertEquals(1L, dataFrame.getTestingData().getCollection().get(0));
    }


    @Test
    void update() {
        generateTransactions();
        assertEquals(10, dataFrame.getTestingData().getCollection().size());
        assertEquals(10, dataFrame.getTrainingData(timestamp).get(0).getCollection().size());
    }
}