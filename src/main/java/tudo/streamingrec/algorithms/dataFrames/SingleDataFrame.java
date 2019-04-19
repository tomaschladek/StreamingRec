package tudo.streamingrec.algorithms.dataFrames;

import tudo.streamingrec.algorithms.dtos.FrameDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SingleDataFrame implements IDataFrame {

    private FrameDto testing;

    public SingleDataFrame() {
        this.testing = new FrameDto();
    }

    public List<List<Long>> getTrainingData(Date time)
    {
        List<List<Long>> list = new ArrayList<>();
        list.add(testing.collection);
        return list;
    }

    public List<Long> getTestingData()
    {
        return new ArrayList<>(testing.collection);
    }

    public boolean update(Date timestamp) {
        return false;
    }
}
