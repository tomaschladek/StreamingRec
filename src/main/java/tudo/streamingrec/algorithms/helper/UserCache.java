package tudo.streamingrec.algorithms.helper;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class UserCache {

    private long[] cache;
    private int modulo;
    private int clearingTime;
    private Date clearingThreshold = new Date(0,0,0,0,0,0);


    public UserCache(Integer exponent,int clearingTime) {
        if (exponent == null) return;

        this.modulo = (int)Math.pow(2,exponent);
        cache = new long[this.modulo];
        this.clearingTime = clearingTime;
    }

    public boolean tryUpsert(long userId, long itemId)
    {
        if (cache == null || userId == 0) return true;

        int hash = getHash(userId);
        if (cache[hash] == itemId) return false;

        cache[hash] = itemId;
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
            cache = new long[this.modulo];
        }
    }
}
