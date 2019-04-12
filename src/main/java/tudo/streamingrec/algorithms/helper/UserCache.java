package tudo.streamingrec.algorithms.helper;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserCache {
    private int size;

//    private long[] cache;

    private int modulo;
    private int clearingTime;
    private Date clearingThreshold = new Date(0,0,0,0,0,0);
    private List<CircularFifoQueue<Long>> cache;

    public UserCache(Integer exponent,int clearingTime, int size) {
        if (exponent == null) return;

        this.modulo = (int)Math.pow(2,exponent);
        assignCache();
        this.clearingTime = clearingTime;
        this.size = size;
    }

    protected void assignCache() {
        cache = new ArrayList<>(modulo);
        for(int index = 0; index < modulo; index++)
        {
            cache.add(null);
        }
    }

    public boolean tryUpsert(long userId, long itemId)
    {
        if (cache == null
                || userId == 0)
            return true;

        Short hash = getHash(userId);

        if (cache.get(hash) != null
                && cache.get(hash).contains(itemId))
            return false;

        if (!cache.contains(hash)) {
            cache.set(hash,new CircularFifoQueue<>(size));
        }

        cache.get(hash).add(itemId);
        return true;
    }

    private short getHash(long userId) {
        int hash = 7;
        for (int index = 0; index < 7; index++)
            hash = 31 * hash + (int) userId;
        return (short) Math.floorMod(hash,modulo);
    }

    public void update(Date timestamp) {
        if (cache != null
                && timestamp != null
                && clearingThreshold.before(timestamp))
        {
            clearingThreshold = DateUtils.addMinutes(timestamp,clearingTime);
            assignCache();
        }
    }
}
