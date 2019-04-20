package tudo.streamingrec.algorithms.filters;

import tudo.streamingrec.algorithms.helper.UserCache;
import tudo.streamingrec.data.Item;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class CacheFilter implements IFilter {
    private UserCache userCache;

    public CacheFilter(UserCache userCache) {
        this.userCache = userCache;
    }

    @Override
    public void extendFilter(long userId, long itemFrom, Set<Long> forbiddenIds, List<Long> allowedIds, List<Item> items) {
        if (forbiddenIds == null) return;

        forbiddenIds.addAll(userCache.getHistory(userId));
    }

    @Override
    public void train(long userId, long itemFrom, Date timestamp) {

        userCache.update(timestamp);
        userCache.tryUpsert(userId,itemFrom);
    }

    @Override
    public void trainFromRecommendation(long userId, long itemFrom, long itemTo) {
        userCache.tryUpsert(userId,itemTo);
    }

    @Override
    public IFilter copy() {
        return new CacheFilter(userCache.copy());
    }
}
