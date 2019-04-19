package tudo.streamingrec.algorithms.dataFrames;

import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeparateTimingDataFrame extends AbstractTwoTimingDataFrame {
    public SeparateTimingDataFrame(int[] timeFrame) {
        super(timeFrame);
    }

    public List<List<Long>> getTrainingData(Date time)
    {
        List<List<Long>> list = new ArrayList<>();
        list.add(training.collection);
        return list;
    }

    public boolean update(Date timestamp) {

        if (configuration != null
                && timestamp != null
                && testing.timestampThreshold.before(timestamp))
        {
            while(testing.timestampThreshold.before(timestamp)) {
                testing.timestampThreshold = DateUtils.addMinutes(testing.timestampThreshold, configuration.getNext());
            }
            testing.assignAndClear(training);
            return true;
        }
        return false;
    }
}
