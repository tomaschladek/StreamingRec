package tudo.streamingrec.algorithms.dataFrames;

import java.util.Date;
import java.util.List;

public interface IDataFrame {
    
    List<List<Long>> getTrainingData(Date time);
    List<Long> getTestingData();
    boolean update(Date timestamp);
}
