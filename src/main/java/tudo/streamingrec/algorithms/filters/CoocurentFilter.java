package tudo.streamingrec.algorithms.filters;

import org.apache.commons.lang3.time.DateUtils;
import tudo.streamingrec.data.Item;

import java.util.*;

public class CoocurentFilter implements IFilter {

    private final int secondsToExpire;
    private Date expirationTime = new Date(0);
    private Map<Long,Set<Long>> coocurence;

    public CoocurentFilter(int secondsToExpire) {
        this.coocurence = new HashMap<>();
        this.secondsToExpire = secondsToExpire;
    }

    @Override
    public void extendFilter(long userId, long itemFrom, Set<Long> forbiddenIds, List<Long> allowedIds, List<Item> items) {
        if (allowedIds == null) return;

        if (coocurence.containsKey(itemFrom)) {
            List<Long> intersection = new ArrayList<>(coocurence.get(itemFrom));
            intersection.add(itemFrom);
            allowedIds.retainAll(intersection);
        }
    }

    @Override
    public void train(long userId, long itemFrom, Date timestamp) {
        if (expirationTime.before(timestamp))
        {
            expirationTime = DateUtils.addSeconds(timestamp, secondsToExpire);
            coocurence = new HashMap<>();
        }
    }

    @Override
    public void trainFromRecommendation(long userId, long itemFrom, long itemTo) {
        addCoocurence(itemFrom, itemTo);
        addCoocurence(itemTo, itemFrom);
    }

    private void addCoocurence(long itemFrom, long itemTo) {
        if (!coocurence.containsKey(itemFrom))
        {
            coocurence.put(itemFrom,new HashSet<>());
        }
        coocurence.get(itemFrom).add(itemTo);
    }
}
