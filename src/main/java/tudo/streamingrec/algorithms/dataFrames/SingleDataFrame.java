package tudo.streamingrec.algorithms.dataFrames;

import tudo.streamingrec.algorithms.streaming.IStreamingExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SingleDataFrame implements IDataFrame {

    private IStreamingExecutor executor;

    public List<IStreamingExecutor> getTrainingData(Date time)
    {
        List<IStreamingExecutor> list = new ArrayList<>();
        list.add(executor);
        return list;
    }

    public IStreamingExecutor getTestingData()
    {
        return executor;
    }

    public boolean update(Date timestamp) {
        return false;
    }

    @Override
    public void assignExecutor(IStreamingExecutor executor) {
        this.executor = executor;
    }
}
