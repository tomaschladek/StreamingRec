package tudo.streamingrec.algorithms.dataFrames;

import tudo.streamingrec.algorithms.dtos.FrameConfiguration;
import tudo.streamingrec.algorithms.dtos.FrameTimeDto;

import java.util.List;

public abstract class AbstractTwoTimingDataFrame implements IDataFrame {
    protected FrameConfiguration configuration;
    protected FrameTimeDto training;
    protected FrameTimeDto testing;

    public AbstractTwoTimingDataFrame(int[] timeFrame) {
        this.configuration = timeFrame.length > 0
                ? new FrameConfiguration(timeFrame)
                : null;
        this.training = new FrameTimeDto();
        this.testing = new FrameTimeDto();
    }

    public List<Long> getTestingData()
    {
        return testing.collection;
    }
}
