package tudo.streamingrec.algorithms.helper;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserCache {
    private int size;
    private int capacity;
    private int clearingTime;
    private Date clearingThreshold = new Date(0, 0, 0, 0, 0, 0);
    private List<CircularFifoQueue<Long>> cache;

    public UserCache(int exponent, int clearingTime, int size) {
        this.capacity = (int) Math.pow(2, exponent);
        assignCache();
        this.clearingTime = clearingTime;
        this.size = size;
    }

    private void assignCache() {
        cache = new ArrayList<>(capacity);
        for (int index = 0; index < capacity; index++) {
            cache.add(null);
        }
    }

    public boolean tryUpsert(long userId, long itemId) {
        if (cache == null
                || userId == 0)
            return true;

        Short hash = getHash(userId);

        if (cache.get(hash) != null
                && cache.get(hash).contains(itemId))
            return false;

        if (cache.get(hash) == null) {
            cache.set(hash, new CircularFifoQueue<>(size));
        }

        cache.get(hash).add(itemId);
        return true;
    }

    private short getHash(long userId) {
        int hash = 7;
        for (int index = 0; index < 7; index++)
            hash = 31 * hash + (int) userId;
        return (short) Math.floorMod(hash, capacity);
    }

    public void update(Date timestamp) {
        if (cache != null
                && timestamp != null
                && clearingThreshold.before(timestamp)) {

            clearingThreshold = DateUtils.addSeconds(timestamp, clearingTime);
            assignCache();
        }
    }

    public CircularFifoQueue<Long> getHistory(long userId) {
        if (cache == null
                || userId == 0)
            return new CircularFifoQueue<>(1);

        Short hash = getHash(userId);

        if (cache.get(hash) != null) return cache.get(hash);

        return new CircularFifoQueue<>(1);
    }

    public UserCache copy() {
        return new UserCache((int) (Math.log(capacity) / Math.log(2)),clearingTime,size);
    }
}