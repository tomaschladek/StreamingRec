package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.time.DateUtils;
import tudo.streamingrec.algorithms.helper.EHeuristic;
import tudo.streamingrec.algorithms.helper.UserCache;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.Date;
import java.util.List;

public class PopularityBased extends Algorithm {
    // In this list we keep all articles and their click counts
    protected Long2IntOpenHashMap clickCounter = new Long2IntOpenHashMap();
    private Date windowThreshold = new Date(0,0,0,0,0,0);
    protected int[] countMax = new int[]{100};
    private int countIndex = 0;
    private boolean isTimeDriven = false;
    private int countCurrent = 0;
    private long index = 0;
    private long count = Long.MIN_VALUE;

    protected EHeuristic heuristic = EHeuristic.PopularTopDown;
    private java.util.Random generator = new java.util.Random();
    private CircularFifoQueue<Long> clicks = new CircularFifoQueue<>(50);

    protected Integer userCacheExponent = 10;
    protected int clearingTime = 1;
    protected int cacheDepth = 1;

    protected UserCache userCache = new UserCache(userCacheExponent, clearingTime,cacheDepth);

    @Override
    protected void trainInternal(List<Item> items, List<ClickData> clickData) {
        Date timestamp = null;

        for (ClickData c : clickData) {
            timestamp = c.click.timestamp;
            clickCounter.addTo(c.click.item.id, 1);
            countCurrent++;
            clicks.add(c.click.item.id);
            if (clickCounter.get(c.click.item.id) > count)
            {
                index = c.click.item.id;
                count = clickCounter.get(c.click.item.id);
            }
        }
        userCache.update(timestamp);
        if ((!isTimeDriven && countCurrent > countMax[countIndex])
                || (isTimeDriven && timestamp != null && windowThreshold.before(timestamp)))
        {
            clickCounter = new Long2IntOpenHashMap();
            clickCounter.addTo(index,1);
            count = 1;
            countCurrent = 0;
            windowThreshold = DateUtils.addMinutes(timestamp,countMax[countIndex]);
            countIndex = (countIndex + 1) % countMax.length;
        }
    }

    public LongArrayList recommendInternal(ClickData clickData) {
        //return the items sorted by their click count
        long recommendedValue = getRecommendedValue(clickData.click.userId,clickData.click.item.id);
        LongArrayList list = new LongArrayList();
        list.add(recommendedValue);
        return list;
    }
    protected long getRecommendedValue(long userId, long itemId) {


        if (userCache.tryUpsert(userId,index)
                && itemId != index)
            return index;

        CircularFifoQueue<Long> used = userCache.getHistory(userId);
        switch (heuristic)
        {
            case PopularTopDown:
                return getTopDownBest(used, itemId);
            case Random:
                return getRandomFirst(used,itemId);
            case RecentClicks:
                return getRecentClick(used,itemId);
        }
        throw new IllegalArgumentException("Unknown heuristic!");

    }

    private long getRecentClick(CircularFifoQueue<Long> used, long itemId) {
        for (Long value : clicks) {
            if (!used.contains(value)
                    && value != itemId
                    && value != index)
            {
                return value;
            }
        }
        return index;
    }

    private long getRandomFirst(CircularFifoQueue<Long> used, long itemId) {

        for (int index = 0; index < 30; index++)
        {
            int randIndex = generator.nextInt(clickCounter.size());
            LongIterator iterator = clickCounter.keySet().iterator();
            long value = 0;
            for (int clickIndex = 0; clickIndex < randIndex; clickIndex++)
            {
                value = iterator.nextLong();
            }
            if (!used.contains(value)
                    && value != itemId
                    && value != index)
            {
                return value;
            }
        }
        return index;
    }

    private long getTopDownBest(CircularFifoQueue<Long> used, long itemId) {
        int max = 0;
        Long maxKey = index;
        for (Long key: clickCounter.keySet()) {
            int value = clickCounter.get(key);
            if (value > max
                    && !used.contains(key.longValue())
                    && key.longValue() != itemId
                    && key.longValue() != index)
            {
                maxKey = key;
                max = value;
            }
        }

        return maxKey;
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
    public void setCountMax(String countMax) {
        String[] frames = countMax.split(",");
        this.countMax = new int[frames.length];
        for (int index = 0; index < frames.length; index++) {
            this.countMax[index] = Integer.parseInt(frames[index]);
        }
    }

    /**
     * Defines the size of the reservoir
     * @param isTimeDriven -
     */
    public void setIsTimeDriven(boolean isTimeDriven) {
        this.isTimeDriven = isTimeDriven;
    }


    protected void assignCache() {
        userCache = new UserCache(userCacheExponent, clearingTime,cacheDepth);
    }

    /**
     * Defines the size of the reservoir
     * @param heuristic -
     */
    public void setHeuristic(String heuristic) {
        switch (heuristic){
            case "random":
                this.heuristic = EHeuristic.Random;
                break;
            case "popular":
                this.heuristic = EHeuristic.PopularTopDown;
                break;
            case "click":
                this.heuristic = EHeuristic.RecentClicks;
                break;
            default:
                throw new IllegalArgumentException("mode not recognized");
        }
    }
}
