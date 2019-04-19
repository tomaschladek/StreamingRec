package testing.streamingrec.algorithms.dataFrames;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import tudo.streamingrec.algorithms.dataFrames.IDataFrame;

import java.util.Date;
import java.util.List;

public class AbstractDataFrameTest {

    protected IDataFrame dataFrame;
    protected Date timestamp;

    @BeforeEach
    public void setUp(){
        this.timestamp = new Date(2,0,0,0,0,0);
    }

    protected void generateTransactions() {
        for (long index = 0; index < 15; index++) {
            for (List<Long> set : dataFrame.getTrainingData(timestamp))
                set.add(index);
            timestamp = DateUtils.addMinutes(timestamp,1);
            dataFrame.update(timestamp);
        }
    }
}
