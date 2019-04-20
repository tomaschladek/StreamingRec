package tudo.streamingrec.algorithms.filters;

import tudo.streamingrec.algorithms.helper.UserCache;
import tudo.streamingrec.data.Item;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class CacheFilter extends UserCache implements IFilter {

    public CacheFilter(int exponent, int clearingTime, int size){
        super(exponent,clearingTime,size);
    }

    @Override
    public void extendFilter(long userId, long itemFrom, Set<Long> forbiddenIds, List<Long> allowedIds, List<Item> items) {
        if (forbiddenIds == null) return;

        forbiddenIds.addAll(getHistory(userId));
    }

    @Override
    public void train(long userId, long itemFrom, Date timestamp) {

        update(timestamp);
        tryUpsert(userId,itemFrom);
    }

    @Override
    public void trainFromRecommendation(long userId, long itemFrom, long itemTo) {

        tryUpsert(userId,itemTo);
    }
}
