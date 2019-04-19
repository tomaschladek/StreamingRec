package tudo.streamingrec.algorithms.dataFrames;

import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OverlappingTimingDataFrame extends AbstractTwoTimingDataFrame {
    private int trainingTime;

    public OverlappingTimingDataFrame(int[] timeFrame, int trainingTime) {
        super(timeFrame);
        this.trainingTime = trainingTime;
    }

    public List<List<Long>> getTrainingData(Date time)
    {
        List<List<Long>> list = new ArrayList<>();
        list.add(testing.collection);
        if (training.timestampThreshold.before(time))
        {
            list.add(training.collection);
        }
        return list;
    }

    public boolean update(Date timestamp) {
        if (configuration != null
                && timestamp != null
                && testing.timestampThreshold.before(timestamp))
        {
            while(testing.timestampThreshold.before(timestamp)) {
                testing.timestampThreshold = DateUtils.addMinutes(testing.timestampThreshold, configuration.getNext());
                training.timestampThreshold = DateUtils.addMinutes(testing.timestampThreshold, -trainingTime);
            }
            testing.assignAndClear(training);
            return true;
        }
        return false;
    }
}
