package testing.streamingrec.algorithms.dataFrames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tudo.streamingrec.algorithms.dataFrames.SingleDataFrame;
import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SingleDataFrameTest extends AbstractDataFrameTest {

    @BeforeEach
    public void setUp() {
        this.dataFrame = new SingleDataFrame();
        super.setUp();
    }

    @Test
    void getTrainingData() {
        assertEquals(1,dataFrame.getTrainingData(timestamp).size());
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
        assertEquals(15, dataFrame.getTestingData().getCollection().size());
        assertEquals(15, dataFrame.getTrainingData(timestamp).get(0).getCollection().size());

    }
}