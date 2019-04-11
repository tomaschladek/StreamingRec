package tudo.streamingrec.algorithms.helper;

import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataFrameManager {
    private int capacity;
    private int timeFrameIndex = 0;
    private int[] timeFrame = new int[]{};
    private int trainingTime;
    private List<Long> sampleTraining = new ArrayList<>();
    private List<Long> sampleTesting = new ArrayList<>();
    private EFraming mode;
    private Date trainThreshold = new Date(0,0,0,0,0,0);
    private Date frameThreshold = new Date(0,0,0,0,0,0);

    public DataFrameManager(int[] timeFrame, int capacity, EFraming mode, int trainingTime) {
        this.timeFrame = timeFrame;
        this.capacity = capacity;
        this.trainingTime = trainingTime;
        switch (mode)
        {
            case SingleModel:
                this.sampleTraining = this.sampleTesting = new ArrayList<>(capacity);
                break;
            case OverlappingModels:
            case SeparateModels:
                this.sampleTraining = new ArrayList<>(capacity);
                this.sampleTesting = new ArrayList<>(capacity);
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
                list.add(sampleTesting);
                break;
            case OverlappingModels:
                list.add(sampleTesting);
                if (trainThreshold.before(time))
                {
                    list.add(sampleTraining);
                }
                break;
            case SeparateModels:
                list.add(sampleTraining);
                break;
        }
        return list;
    }

    public List<Long> getTestingData()
    {
        return sampleTesting;
    }

    public boolean update(Date timestamp) {
        switch (mode)
        {
            case SingleModel:
                break;
            case OverlappingModels:
            case SeparateModels:
                if (timeFrame.length > 0
                        && timestamp != null
                        && frameThreshold.before(timestamp))
                {
                    frameThreshold = DateUtils.addMinutes(timestamp,timeFrame[timeFrameIndex]);
                    trainThreshold = DateUtils.addMinutes(timestamp,timeFrame[timeFrameIndex]-trainingTime);
                    timeFrameIndex = (timeFrameIndex + 1) % timeFrame.length;
                    sampleTesting = sampleTraining;
                    sampleTraining = new ArrayList<>(capacity);
                    return true;
                }
                break;
        }
        return false;
    }
}
