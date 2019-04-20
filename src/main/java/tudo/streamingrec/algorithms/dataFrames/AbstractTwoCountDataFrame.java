package tudo.streamingrec.algorithms.dataFrames;

import tudo.streamingrec.algorithms.dtos.FrameConfiguration;
import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

public abstract class AbstractTwoCountDataFrame implements IDataFrame{
    protected FrameConfiguration configuration;
    protected IStreamingExecutor training;
    protected IStreamingExecutor testing;
    protected int testingCount;

    AbstractTwoCountDataFrame(int[] timeFrame) {
        this.configuration = new FrameConfiguration(timeFrame);
        this.testingCount = configuration.getNext();
    }

    public IStreamingExecutor getTestingData()
    {
        if (testing.getCollection().size() != 0)
            return testing;
        return training;
    }



    public void assignAndClear() {
        testing = training;
        testingCount = configuration.getNext();
        training = training.copy();
    }

    @Override
    public void assignExecutor(IStreamingExecutor executor) {
        this.training = executor;
        this.testing = executor.copy();
    }
}
