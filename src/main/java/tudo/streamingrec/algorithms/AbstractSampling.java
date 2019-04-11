package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import tudo.streamingrec.algorithms.helper.AbstractReservoirSampler;
import tudo.streamingrec.algorithms.helper.DataFrameManager;
import tudo.streamingrec.algorithms.helper.EFraming;
import tudo.streamingrec.algorithms.helper.ReservoirSamplerDynamic;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.Date;
import java.util.List;

public abstract class AbstractSampling extends Algorithm {

    protected AbstractReservoirSampler<Long> sampler = new ReservoirSamplerDynamic<>(1);
    protected boolean areClicksUsed = true;
    protected int reservoirSize = 30;
    protected EFraming mode = EFraming.SingleModel;
    private int[] timeFrame = new int[]{};
    private int trainingTime = 30;
    protected DataFrameManager dataFrameManager = new DataFrameManager(timeFrame,reservoirSize, mode, trainingTime);

    @Override
    protected void trainInternal(List<Item> items, List<ClickData> clickData) {
        Date timestamp = null;
        if (areClicksUsed)
        {
            for (ClickData data : clickData)
            {
                timestamp = data.click.timestamp;
                for (List<Long> dataset: dataFrameManager.getTrainingData(timestamp)){
                    sampler.add(dataset,data.click.item.id);
                }
            }
        }
        else{
            for (Item item : items) {
                timestamp = item.updatedAt;
                for (List<Long> dataset: dataFrameManager.getTrainingData(timestamp)){
                    sampler.add(dataset,item.id);
                }
            }
        }
        if (dataFrameManager.update(timestamp))
        {
            assignReservoir();
        }

    }

    protected abstract void assignReservoir();

    @Override
    public LongArrayList recommendInternal(ClickData clickData) {
        LongArrayList list = new LongArrayList(1);
        list.add(sampler.get(dataFrameManager.getTestingData()).longValue());
        return list;
    }


    /**
     * Defines the size of the reservoir
     * @param reservoirSize -
     */
    public void setReservoirSize(int reservoirSize) {
        this.reservoirSize = reservoirSize;
        assignDataFrame();
        assignReservoir();
    }

    protected void assignDataFrame() {
        dataFrameManager = new DataFrameManager(timeFrame,reservoirSize,mode,trainingTime);
    }

    /**
     * Defines the size of the reservoir
     * @param areClicksUsed -
     */
    public void setAreClicksUsed(boolean areClicksUsed) {
        this.areClicksUsed = areClicksUsed;
    }

    /**
     * Defines the size of the reservoir
     * @param mode -
     */
    public void setMode(String mode) {
        switch (mode){
            case "single":
                this.mode = EFraming.SingleModel;
                break;
            case "separate":
                this.mode = EFraming.SeparateModels;
                break;
            case "overlap":
                this.mode = EFraming.OverlappingModels;
                break;
            default:
                throw new IllegalArgumentException("mode not recognized");
        }
        assignDataFrame();


    }

    /**
     * Defines the size of the reservoir
     * @param trainingTime -
     */
    public void setTrainingTime(int trainingTime) {
        this.trainingTime = trainingTime;
        assignDataFrame();
    }

    /**
     * Defines the size of the reservoir
     * @param timeFrame -
     */
    public void setTimeFrame(String timeFrame) {
        String[] frames = timeFrame.split(",");
        this.timeFrame = new int[frames.length];
        for (int index = 0; index < frames.length; index++) {
            this.timeFrame[index] = Integer.parseInt(frames[index]);
        }
        assignDataFrame();
    }
}
