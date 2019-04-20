package testing.streamingrec.algorithms.dataFrames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tudo.streamingrec.algorithms.dataFrames.OverlappingCountDataFrame;
import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OverlappingCountDataFrameTest extends AbstractDataFrameTest {

    @BeforeEach
    public void setUp() {
        dataFrame = new OverlappingCountDataFrame(new int[]{10,20,30},5);
        super.setUp();

    }

    @Test
    void getTrainingData() {
        List<IStreamingExecutor> trainExecutors = dataFrame.getTrainingData(timestamp);
        assertEquals(1, trainExecutors.size());
        assertEquals(0, trainExecutors.get(0).getCollection().size());
    }

    @Test
    void getTestingData() {
        IStreamingExecutor testingData = dataFrame.getTestingData();
        assertEquals(0, testingData.getCollection().size());
        for (IStreamingExecutor executor : dataFrame.getTrainingData(timestamp))
        {
            executor.getCollection().add(1L);
        }
        assertEquals(1, dataFrame.getTestingData().getCollection().size());
        assertEquals(1L, dataFrame.getTestingData().getCollection().get(0));
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
        assertEquals(10, dataFrame.getTestingData().getCollection().size());
        assertEquals(10, dataFrame.getTrainingData(timestamp).get(0).getCollection().size());
    }
}