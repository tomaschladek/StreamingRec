package tudo.streamingrec.algorithms.dataFrames;

import tudo.streamingrec.algorithms.dtos.FrameConfiguration;
import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import java.util.Date;

public abstract class AbstractTwoTimingDataFrame implements IDataFrame {
    protected FrameConfiguration configuration;
    protected IStreamingExecutor training;
    protected Date trainingTimestamp = new Date(0);
    protected IStreamingExecutor testing;
    protected Date testingTimestamp = new Date(0);

    AbstractTwoTimingDataFrame(int[] timeFrame) {
        this.configuration = timeFrame.length > 0
                ? new FrameConfiguration(timeFrame)
                : null;
    }

    public IStreamingExecutor getTestingData()
    {
        return testing;
    }


    public void assignAndClear() {
        testing = training;
        training = training.copy();
    }


    @Override
    public void assignExecutor(IStreamingExecutor executor) {
        this.training = executor;
        this.testing = executor.copy();
    }
}
