package testing.streamingrec.algorithms.dataFrames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tudo.streamingrec.algorithms.dataFrames.SeparateCountDataFrame;
import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SeparateCountDataFrameTest extends AbstractDataFrameTest {

    @BeforeEach
    public void setUp() {
        this.dataFrame = new SeparateCountDataFrame(new int[]{10,20,30});
        super.setUp();
    }

    @ParameterizedTest
    @CsvSource({"1,2","2,2","3,2","4,2","5,2","6,2","7,2","8,2","9,2","10,1",})
    void getTrainingData(int max, int expectedValue) {
        assertEquals(2, dataFrame.getTrainingData(timestamp).size());
        for(long index = 0; index < max; index++) {
            for (IStreamingExecutor datasets : dataFrame.getTrainingData(timestamp)) {
                datasets.getCollection().add(1L);
            }
            dataFrame.update(timestamp);
        }
        assertEquals(expectedValue, dataFrame.getTrainingData(timestamp).size());
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
    }

    @Test
    void update() {
        generateTransactions();
        assertEquals(10, dataFrame.getTestingData().getCollection().size());
        assertEquals(5, dataFrame.getTrainingData(timestamp).get(0).getCollection().size());
    }
}