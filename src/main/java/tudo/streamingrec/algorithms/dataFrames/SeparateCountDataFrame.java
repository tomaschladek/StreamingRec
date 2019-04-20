package tudo.streamingrec.algorithms.dataFrames;

import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeparateCountDataFrame extends AbstractTwoCountDataFrame {

    private boolean isFirstRun = true;

    public SeparateCountDataFrame(int[] timeFrame) {
        super(timeFrame);
    }

    public List<IStreamingExecutor> getTrainingData(Date time)
    {
        List<IStreamingExecutor> list = new ArrayList<>();
        if (isFirstRun)
        {
            list.add(testing);
        }
        list.add(training);
        return list;
    }

    public boolean update(Date timestamp) {

        if (configuration != null
                && timestamp != null
                && testingCount <= testing.getCollection().size())
        {
            isFirstRun = false;
            assignAndClear();
            return true;
        }
        return false;
    }
}
