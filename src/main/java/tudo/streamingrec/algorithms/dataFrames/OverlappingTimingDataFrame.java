package tudo.streamingrec.algorithms.dataFrames;

import org.apache.commons.lang3.time.DateUtils;
import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OverlappingTimingDataFrame extends AbstractTwoTimingDataFrame {
    private int trainingTime;

    public OverlappingTimingDataFrame(int[] timeFrame, int trainingTime) {
        super(timeFrame);
        this.trainingTime = trainingTime;
    }

    public List<IStreamingExecutor> getTrainingData(Date time)
    {
        List<IStreamingExecutor> list = new ArrayList<>();
        list.add(testing);
        if (trainingTimestamp.before(time))
        {
            list.add(training);
        }
        return list;
    }

    public boolean update(Date timestamp) {
        if (configuration != null
                && timestamp != null
                && testingTimestamp.before(timestamp))
        {
            while(testingTimestamp.before(timestamp)) {
                testingTimestamp = DateUtils.addMinutes(testingTimestamp, configuration.getNext());
                trainingTimestamp = DateUtils.addMinutes(testingTimestamp, -trainingTime);
            }
            assignAndClear();
            return true;
        }
        return false;
    }
}
