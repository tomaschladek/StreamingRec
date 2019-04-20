package testing.streamingrec.algorithms.dataFrames;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import tudo.streamingrec.algorithms.dataFrames.IDataFrame;
import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import java.util.ArrayList;
import java.util.Date;

public class AbstractDataFrameTest {

    @Mock
    IStreamingExecutor executor;

    protected IDataFrame dataFrame;
    protected Date timestamp;

    @BeforeEach
    public void setUp(){
        this.timestamp = new Date(1000*60*60*24*356*2);
        Mockito.when(executor.getCollection()).thenReturn(new ArrayList<>());
        Mockito.when(executor.copy()).thenAnswer(createMockCopy());
        dataFrame.assignExecutor(executor);
    }

    protected Answer<IStreamingExecutor> createMockCopy() {
        return invocationOnMock -> {
            IStreamingExecutor mock = Mockito.mock(IStreamingExecutor.class);
            Mockito.when(mock.getCollection()).thenReturn(new ArrayList<>());
            Mockito.when(mock.copy()).thenAnswer(createMockCopy());
            return mock;
        };
    }

    protected void generateTransactions() {
        for (long index = 0; index < 15; index++) {
            for (IStreamingExecutor executor : dataFrame.getTrainingData(timestamp))
                executor.getCollection().add(index);
            timestamp = DateUtils.addMinutes(timestamp,1);
            dataFrame.update(timestamp);
        }
    }
}
