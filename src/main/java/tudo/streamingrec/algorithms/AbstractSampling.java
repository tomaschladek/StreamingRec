package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import tudo.streamingrec.algorithms.dataFrames.IDataFrame;
import tudo.streamingrec.algorithms.dataFrames.OverlappingCountDataFrame;
import tudo.streamingrec.algorithms.dataFrames.SeparateCountDataFrame;
import tudo.streamingrec.algorithms.dataFrames.SingleDataFrame;
import tudo.streamingrec.algorithms.dtos.EFraming;
import tudo.streamingrec.algorithms.helper.UserCache;
import tudo.streamingrec.algorithms.heuristics.IHeuristic;
import tudo.streamingrec.algorithms.heuristics.IteratorHeuristic;
import tudo.streamingrec.algorithms.samplers.AbstractReservoirSampler;
import tudo.streamingrec.algorithms.samplers.DynamicReservoirSampler;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.*;

public abstract class AbstractSampling extends Algorithm {

    AbstractReservoirSampler sampler = new DynamicReservoirSampler(1);
    private boolean areClicksUsed = true;

    private Integer userCacheExponent = null;
    private int clearingTime = 10;
    private int cacheDepth = 1;

    private EFraming mode = EFraming.SingleModel;

    protected int reservoirSize = 30;

    private int[] timeFrame = new int[]{};
    private int trainingTime = 30;

    private IDataFrame dataFrameManager = new OverlappingCountDataFrame(timeFrame, trainingTime);
    private UserCache userCache = new UserCache(userCacheExponent, clearingTime,cacheDepth);
    private IHeuristic heuristic = new IteratorHeuristic();


    private Long2IntOpenHashMap clickCounter = new Long2IntOpenHashMap();
    private long clickedItem = 0L;
    private int countMax = 1000;
    private int countCurrent = 0;
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
            if(clickData != null)
            for (ClickData data : clickData)
            {
                if (data.click.userId == 0) return;
                timestamp = data.click.timestamp;
                clickCounter.addTo(data.click.item.id, 1);
                countCurrent++;
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
                    countCurrent++;
                }
            }
        }
        dataFrameManager.update(timestamp);
        userCache.update(timestamp);
        if (countCurrent > countMax)
        {
            clickCounter = new Long2IntOpenHashMap();
            clickCounter.addTo(index,1);
            count = 1;
            countCurrent = 0;
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

        Set<Long> forbidden = new HashSet<>();
        forbidden.add(itemId);
        Long recommendedValue = heuristic.get(testingData,forbidden);
        if (recommendedValue != null
                && userCache.tryUpsert(userId,recommendedValue)
                && itemId != recommendedValue)
            return recommendedValue;

        if ((recommendedValue == null
                || index != recommendedValue)
            && itemId != index)
            return index;

        //return clickedItem;
        int max = 0;
        Long maxKey = recommendedValue;
        for (Long key: clickCounter.keySet()) {
            int value = clickCounter.get(key.longValue());
            if (value>max
                && (recommendedValue == null
                    || (key.longValue() != recommendedValue
                        && itemId != recommendedValue)
                    )
                )
            {
                maxKey = key;
                max = value;
            }
        }
        if (maxKey == null)
            return itemId;
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

    private void assignDataFrame() {
        switch (mode){
            case SingleModel:
                dataFrameManager = new SingleDataFrame();
                break;
            case OverlappingModels:
                dataFrameManager = new OverlappingCountDataFrame(timeFrame, trainingTime);
                break;
            case SeparateModels:
                dataFrameManager = new SeparateCountDataFrame(timeFrame);
                break;
        }
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
        assignCache();
    }

    /**
     * Defines the size of the reservoir
     * @param clearingTime -
     */
    public void setClearingTime(int clearingTime) {
        this.clearingTime = clearingTime;
        assignCache();
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
     * @param cacheDepth -
     */
    public void setCacheDepth(int cacheDepth) {
        this.cacheDepth = cacheDepth;
        assignCache();
    }

    /**
     * Defines the size of the reservoir
     * @param countMax -
     */
    public void setCountMax(int countMax) {
        this.countMax = countMax;
    }

    private void assignCache() {
        userCache = new UserCache(userCacheExponent, clearingTime,cacheDepth);
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
