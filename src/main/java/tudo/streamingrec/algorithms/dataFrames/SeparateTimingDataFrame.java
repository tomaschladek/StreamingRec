package tudo.streamingrec.algorithms.dataFrames;

import org.apache.commons.lang3.time.DateUtils;
import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeparateTimingDataFrame extends AbstractTwoTimingDataFrame {
    public SeparateTimingDataFrame(int[] timeFrame) {
        super(timeFrame);
    }

    public List<IStreamingExecutor> getTrainingData(Date time)
    {
        List<IStreamingExecutor> list = new ArrayList<>();
        list.add(training);
        return list;
    }

    public boolean update(Date timestamp) {

        if (configuration != null
                && timestamp != null
                && testingTimestamp.before(timestamp))
        {
            while(testingTimestamp.before(timestamp)) {
                testingTimestamp = DateUtils.addSeconds(testingTimestamp, configuration.getNext());
            }
            assignAndClear();
            return true;
        }
        return false;
    }
}
