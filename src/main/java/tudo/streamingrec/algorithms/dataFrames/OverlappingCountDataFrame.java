package tudo.streamingrec.algorithms.dataFrames;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OverlappingCountDataFrame extends AbstractTwoCountDataFrame {
    private int trainingSize;

    public OverlappingCountDataFrame(int[] timeFrame, int trainingSize) {
        super(timeFrame);
        this.trainingSize = trainingSize;
    }

    public List<List<Long>> getTrainingData(Date time)
    {
        List<List<Long>> list = new ArrayList<>();
        list.add(testing.collection);
        if (testing.count - testing.collection.size() <= trainingSize)
        {
            list.add(training);
        }
        return list;
    }

    public boolean update(Date timestamp) {
        if (configuration != null
                && timestamp != null
                && testing.count <= testing.collection.size())
        {
            testing.assignAndClear(training,configuration.getNext());
            training = new ArrayList<>();
            return true;
        }
        return false;
    }
}
