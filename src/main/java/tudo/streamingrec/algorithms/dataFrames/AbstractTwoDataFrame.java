package tudo.streamingrec.algorithms.dataFrames;

import tudo.streamingrec.algorithms.dtos.FrameConfiguration;
import tudo.streamingrec.algorithms.dtos.FrameDto;

import java.util.List;

public abstract class AbstractTwoDataFrame implements IDataFrame {
    protected FrameConfiguration configuration;
    protected FrameDto training;
    protected FrameDto testing;

    public AbstractTwoDataFrame(int[] timeFrame, int capacity) {
        this.configuration = timeFrame.length > 0
                ? new FrameConfiguration(timeFrame)
                : null;
        this.training = new FrameDto(capacity);
        this.testing = new FrameDto(capacity);
    }

    public List<Long> getTestingData()
    {
        return testing.collection;
    }
}
