package tudo.streamingrec.algorithms.helper;

import org.apache.commons.lang3.time.DateUtils;
import tudo.streamingrec.algorithms.dtos.EFraming;
import tudo.streamingrec.algorithms.dtos.FrameConfiguration;
import tudo.streamingrec.algorithms.dtos.FrameDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Manages frames for testing and training according to selected mode
 */
public class DataFrameManager {
    private FrameConfiguration configuration;
    private int trainingTime;
    private FrameDto training;
    private FrameDto testing;
    private EFraming mode;

    public DataFrameManager(int[] timeFrame, int capacity, EFraming mode, int trainingTime) {
        this.configuration = timeFrame.length > 0
                ? new FrameConfiguration(timeFrame)
                : null;
        this.trainingTime = trainingTime;
        switch (mode)
        {
            case SingleModel:
                this.training = this.testing = new FrameDto(capacity);
                break;
            case OverlappingModels:
            case SeparateModels:
                this.training = new FrameDto(capacity);
                this.testing = new FrameDto(capacity);
                break;
        }
        this.mode = mode;
    }

    public List<List<Long>> getTrainingData(Date time)
    {
        List<List<Long>> list = new ArrayList<>();
        switch (mode)
        {
            case SingleModel:
                list.add(testing.collection);
                break;
            case OverlappingModels:
                list.add(testing.collection);
                if (training.timestampThreshold.before(time))
                {
                    list.add(training.collection);
                }
                break;
            case SeparateModels:
                list.add(training.collection);
                break;
        }
        return list;
    }

    public List<Long> getTestingData()
    {
        return testing.collection;
    }

    public boolean update(Date timestamp) {
        switch (mode)
        {
            case SingleModel:
                break;
            case OverlappingModels:
            case SeparateModels:
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
                break;
        }
        return false;
    }
}
