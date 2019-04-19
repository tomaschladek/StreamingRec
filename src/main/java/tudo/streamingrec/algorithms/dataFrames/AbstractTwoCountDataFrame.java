package tudo.streamingrec.algorithms.dataFrames;

import tudo.streamingrec.algorithms.dtos.FrameConfiguration;
import tudo.streamingrec.algorithms.dtos.FrameCountDto;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTwoCountDataFrame implements IDataFrame{
    protected FrameConfiguration configuration;
    protected List<Long> training;
    protected FrameCountDto testing;

    public AbstractTwoCountDataFrame(int[] timeFrame) {
        this.configuration = new FrameConfiguration(timeFrame);
        this.training = new ArrayList<>();
        this.testing = new FrameCountDto(configuration.getNext());
    }

    public List<Long> getTestingData()
    {
        if (testing.collection.size() != 0)
            return testing.collection;
        return training;
    }
}
