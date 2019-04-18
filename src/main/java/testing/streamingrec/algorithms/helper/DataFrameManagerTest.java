package testing.streamingrec.algorithms.helper;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import tudo.streamingrec.algorithms.helper.DataFrameManager;
import tudo.streamingrec.algorithms.dtos.EFraming;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class DataFrameManagerTest {

    private DataFrameManager manager;
    private Date timestamp;

    void setUp(EFraming mode) {
        this.manager = new DataFrameManager(new int[]{10,20,30},5,mode,5);
        this.timestamp = new Date(2,0,0,0,0,0);
    }

    @ParameterizedTest
    @EnumSource(EFraming.class)
    void getTrainingData(EFraming mode) {
        setUp(mode);
        List<List<Long>> trainingData = manager.getTrainingData(timestamp);
        switch (mode){
            case SingleModel:
            case SeparateModels:
                assertEquals(1, trainingData.size());
                break;
            case OverlappingModels:
                assertEquals(2, trainingData.size());
                assertNotSame(trainingData.get(0),trainingData.get(1));
                break;
        }

    }

    @ParameterizedTest
    @EnumSource(EFraming.class)
    void getTestingData(EFraming mode) {
        setUp(mode);
        List<Long> testingData = manager.getTestingData();
        assertEquals(0, testingData.size());
        for (List<Long> sets : manager.getTrainingData(timestamp))
        {
            sets.add(1l);
        }
        switch (mode){
            case SingleModel:
            case OverlappingModels:
                assertEquals(1, testingData.size());
                assertEquals(1l, testingData.get(0));
                break;
            case SeparateModels:
                assertEquals(0, testingData.size());
                break;
        }
    }

    @ParameterizedTest
    @EnumSource(EFraming.class)
    void update(EFraming mode) {
        setUp(mode);
        generateTransactions();
        assertTransactions(mode);
    }

    private void assertTransactions(EFraming mode) {
        int countTraining =0;
        int countTesting =0;
        switch (mode){
            case SingleModel:
                countTraining = countTesting = 15;
                break;
            case SeparateModels:
                countTraining = 4;
                countTesting = 10;
                /* 1|10|4*/
                break;
            case OverlappingModels:
                countTesting = countTraining = 9;
                /*
                    Frames: 1|10|20
                    Training+Testing=> 1+1 => 5+11 => 4+9
                    Training didnt start in the end. Training = testing.
                */
                break;
        }
        assertEquals(countTesting, manager.getTestingData().size());
        assertEquals(countTraining, manager.getTrainingData(timestamp).get(0).size());
    }

    private void generateTransactions() {
        for (long index = 0; index < 15; index++) {
            for (List<Long> set : manager.getTrainingData(timestamp))
                set.add(index);
            timestamp = DateUtils.addMinutes(timestamp,1);
            manager.update(timestamp);
        }
    }
}