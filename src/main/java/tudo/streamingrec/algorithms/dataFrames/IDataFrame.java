package tudo.streamingrec.algorithms.dataFrames;

import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import java.util.Date;
import java.util.List;

public interface IDataFrame {
    
    List<IStreamingExecutor> getTrainingData(Date time);
    IStreamingExecutor getTestingData();
    boolean update(Date timestamp);

    void assignExecutor(IStreamingExecutor executor);
}
