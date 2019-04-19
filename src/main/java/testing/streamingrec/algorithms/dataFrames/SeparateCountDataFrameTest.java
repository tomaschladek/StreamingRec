package testing.streamingrec.algorithms.dataFrames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tudo.streamingrec.algorithms.dataFrames.SeparateCountDataFrame;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeparateCountDataFrameTest extends AbstractDataFrameTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        this.dataFrame = new SeparateCountDataFrame(new int[]{10,20,30});
    }

    @ParameterizedTest
    @CsvSource({"1,2","2,2","3,2","4,2","5,2","6,2","7,2","8,2","9,2","10,1",})
    void getTrainingData(int max, int expectedValue) {
        assertEquals(2, dataFrame.getTrainingData(timestamp).size());
        for(long index = 0; index < max; index++) {
            for (List<Long> datasets : dataFrame.getTrainingData(timestamp)) {
                datasets.add(1L);
            }
            dataFrame.update(timestamp);
        }
        assertEquals(expectedValue, dataFrame.getTrainingData(timestamp).size());
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
    }

    @Test
    void update() {
        generateTransactions();
        assertEquals(10, dataFrame.getTestingData().size());
        assertEquals(5, dataFrame.getTrainingData(timestamp).get(0).size());
    }
}