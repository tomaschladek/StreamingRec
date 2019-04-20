package tudo.streamingrec.algorithms.dataFrames;

import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OverlappingCountDataFrame extends AbstractTwoCountDataFrame {
    private int trainingSize;

    public OverlappingCountDataFrame(int[] timeFrame, int trainingSize) {
        super(timeFrame);
        this.trainingSize = trainingSize;
    }

    public List<IStreamingExecutor> getTrainingData(Date time)
    {
        List<IStreamingExecutor> list = new ArrayList<>();
        list.add(testing);
        if (testingCount - testing.getCollection().size() <= trainingSize)
        {
            list.add(training);
        }
        return list;
    }

    public boolean update(Date timestamp) {
        if (configuration != null
                && timestamp != null
                && testingCount <= testing.getCollection().size())
        {
            assignAndClear();
            return true;
        }
        return false;
    }
}
