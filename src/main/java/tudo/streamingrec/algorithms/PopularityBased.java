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
import tudo.streamingrec.data.Transaction;

import java.util.*;

/** Algorithm recommends items according to their popularity */
public class PopularityBased extends Algorithm {
    protected Long2IntOpenHashMap clickCounter = new Long2IntOpenHashMap();
    protected Long2IntOpenHashMap trainingCounter = new Long2IntOpenHashMap();
    protected Map<Long,Set<Long>> coocurence = new HashMap<>();
    private Date windowThreshold = new Date(0,0,0,0,0,0);
    private Date trainThreshold = new Date(0,0,0,0,0,0);
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

    protected int trainingSize = 50;

    protected UserCache userCache = new UserCache(userCacheExponent, clearingTime,cacheDepth);

    @Override
    protected void trainInternal(List<Item> items, List<ClickData> clickData) {

        Date timestamp = updateStatistics(clickData);

        userCache.update(timestamp);

        if ((!isTimeDriven && countCurrent > countMax[countIndex])
                || (isTimeDriven && timestamp != null && windowThreshold.before(timestamp)))
        {
            switchWindows(timestamp);
        }
    }

    /** Switches training and recommendation windows according to frames*/
    private void switchWindows(Date timestamp) {
        clickCounter = trainingCounter;
        trainingCounter = new Long2IntOpenHashMap();
        int count = 0;
        for (Long key: clickCounter.keySet()) {
            int value = clickCounter.get(key);
            if (value > count)
            {
                index = key;
                count = value;
            }
        }
        countCurrent = countMax[countIndex] - trainingSize;
        windowThreshold = DateUtils.addMinutes(timestamp,countMax[countIndex]);
        trainThreshold = DateUtils.addMinutes(timestamp,countMax[countIndex] - trainingSize);
        countIndex = (countIndex + 1) % countMax.length;
    }

    private Date updateStatistics(List<ClickData> clickData) {
        Date timestamp = null;

        for (ClickData click : clickData) {
            timestamp = click.click.timestamp;
            clickCounter.addTo(click.click.item.id, 1);
            addCoocurence(click);
            tryAddTraining(timestamp, click);
            countCurrent++;
            clicks.add(click.click.item.id);
            tryUpdateMax(click);
        }
        return timestamp;
    }

    private void addCoocurence(ClickData click) {
        for (Transaction transaction : click.session) {
            if (!coocurence.containsKey(transaction.item.id))
            {
                coocurence.put(new Long(transaction.item.id),new HashSet<>());
            }
            coocurence.get(transaction.item.id).add(click.click.item.id);
        }
    }

    private void tryAddTraining(Date timestamp, ClickData click) {
        if ((!isTimeDriven && countCurrent > countMax[countIndex]-trainingSize)
                || (isTimeDriven && timestamp != null && trainThreshold.before(timestamp)))
        {
            trainingCounter.addTo(click.click.item.id, 1);
        }
    }

    private void tryUpdateMax(ClickData c) {
        if (clickCounter.get(c.click.item.id) > count)
        {
            index = c.click.item.id;
            count = clickCounter.get(c.click.item.id);
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
            case Coocurent:
                return getCoocurentPopular(used,itemId);
        }
        throw new IllegalArgumentException("Unknown heuristic!");

    }

    private long getCoocurentPopular(CircularFifoQueue<Long> used, long itemId) {
        int max = 0;
        Long maxKey = index;
        for (Long key: coocurence.get(index)) {
            if (!clickCounter.containsKey(key)) continue;

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

    /**
     * Defines the size of the reservoir
     * @param trainingSize -
     */
    public void setTrainingSize(int trainingSize) {
        this.trainingSize = trainingSize;
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
            case "coocurent":
                this.heuristic = EHeuristic.Coocurent;
                break;
            default:
                throw new IllegalArgumentException("mode not recognized");
        }
    }
}
