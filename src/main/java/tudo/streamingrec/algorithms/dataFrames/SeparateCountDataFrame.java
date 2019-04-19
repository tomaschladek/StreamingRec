package tudo.streamingrec.algorithms.dataFrames;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeparateCountDataFrame extends AbstractTwoCountDataFrame {

    private boolean isFirstRun = true;

    public SeparateCountDataFrame(int[] timeFrame) {
        super(timeFrame);
    }

    public List<List<Long>> getTrainingData(Date time)
    {
        List<List<Long>> list = new ArrayList<>();
        if (isFirstRun)
        {
            list.add(testing.collection);
        }
        list.add(training);
        return list;
    }

    public boolean update(Date timestamp) {

        if (configuration != null
                && timestamp != null
                && testing.count <= testing.collection.size())
        {
            isFirstRun = false;
            testing.assignAndClear(training, configuration.getNext());
            training = new ArrayList<>();
            return true;
        }
        return false;
    }
}
