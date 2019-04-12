package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import tudo.streamingrec.algorithms.helper.*;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public abstract class AbstractSampling extends Algorithm {

    protected AbstractReservoirSampler sampler = new ReservoirSamplerDynamic(1);
    protected boolean areClicksUsed = true;

    protected Integer userCacheExponent = null;
    protected int clearingTime = 10;

    protected EFraming mode = EFraming.SingleModel;

    protected int reservoirSize = 30;

    private int[] timeFrame = new int[]{};
    private int trainingTime = 30;

    protected DataFrameManager dataFrameManager = new DataFrameManager(timeFrame,reservoirSize, mode, trainingTime);
    protected UserCache userCache = new UserCache(userCacheExponent, clearingTime);

    protected Long2IntOpenHashMap clickCounter = new Long2IntOpenHashMap();
    public long clickedItem = 0l;
    private long index = 0;
    private long count = Long.MIN_VALUE;

    @Override
    protected void trainInternal(List<Item> items, List<ClickData> clickData) {
        Date timestamp = null;
        if (areClicksUsed)
        {
            if (clickData != null
                    && clickData != Collections.EMPTY_LIST
                    && clickData.size() > 0)
            {
                clickedItem = clickData.get(clickData.size()-1).click.item.id;
            }
            for (ClickData data : clickData)
            {
                if (data.click.userId == 0) return;
                timestamp = data.click.timestamp;
                clickCounter.addTo(data.click.item.id, 1);
                if (clickCounter.get(data.click.item.id) > count)
                {
                    index = data.click.item.id;
                    count = clickCounter.get(data.click.item.id);
                }
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
                    clickCounter.addTo(item.id, 1);
                }
            }
        }
        dataFrameManager.update(timestamp);
        userCache.update(timestamp);
        if (count > 1000)
        {
            clickCounter = new Long2IntOpenHashMap();
            clickCounter.addTo(index,1);
            count = 1;
        }

    }

    protected abstract void assignReservoir();

    @Override
    public LongArrayList recommendInternal(ClickData clickData) {

        long recommendedValue = getRecommendedValue(clickData.click.userId,clickData.click.item.id);
        LongArrayList list = new LongArrayList(1);
        list.add(recommendedValue);
        return list;
    }

    protected long getRecommendedValue(long userId, long itemId) {

        List<Long> testingData = dataFrameManager.getTestingData();
        if (testingData.size() == 0) return clickedItem;

        long recommendedValue = sampler.get(testingData).longValue();
        if (userCache.tryUpsert(userId,recommendedValue)
                && itemId != recommendedValue)
            return recommendedValue;

        if (index != recommendedValue
                && itemId != index)
            return index;

        //return clickedItem;
        int max = 0;
        Long maxKey = recommendedValue;
        for (Long key: clickCounter.keySet()) {
            int value = clickCounter.get(key);
            if (value>max
                    && key.longValue() != recommendedValue
                    && itemId != recommendedValue)
            {
                maxKey = key;
                max = value;
            }
        }
        return maxKey;
//            LongArrayList x = (LongArrayList) Util.sortByValueAndGetKeys(clickCounter, false, new LongArrayList());
//            for (Long item: x) {
//                if (item.longValue() != recommendedValue) return item.longValue();
//            }
//            System.out.println("$$$$$ WHAT THE HACK?");
//            System.out.println(x.size());
//            return sampler.get(dataFrameManager.getTestingData(),recommendedValue).longValue();
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
     * @param userCacheExponent -
     */
    public void setUserCacheExponent(int userCacheExponent) {
        this.userCacheExponent = userCacheExponent;
        userCache = new UserCache(userCacheExponent, clearingTime);
    }

    /**
     * Defines the size of the reservoir
     * @param clearingTime -
     */
    public void setClearingTime(int clearingTime) {
        this.clearingTime = clearingTime;
        userCache = new UserCache(userCacheExponent, clearingTime);
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
