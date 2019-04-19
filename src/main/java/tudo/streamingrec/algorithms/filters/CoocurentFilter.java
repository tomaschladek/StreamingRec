package tudo.streamingrec.algorithms.filters;

import tudo.streamingrec.data.Item;

import java.util.*;

public class CoocurentFilter implements IFilter {

    private Map<Long,Set<Long>> coocurence;

    public CoocurentFilter() {
        this.coocurence = new HashMap<>();
    }

    @Override
    public void extendFilter(long userId, long itemFrom, Set<Long> forbiddenIds, Set<Long> allowedIds, List<Item> items) {
        if (allowedIds == null) return;
        if (coocurence.containsKey(itemFrom)) {
            allowedIds.addAll(coocurence.get(itemFrom));
        }
    }

    @Override
    public void train(long userId, long itemFrom) {

    }

    @Override
    public void trainFromRecommendation(long userId, long itemFrom, long itemTo) {
        addCoocurence(itemFrom, itemTo);
        addCoocurence(itemTo, itemFrom);
    }

    private void addCoocurence(long itemFrom, long itemTo) {
        if (!coocurence.containsKey(itemFrom))
        {
            coocurence.put(new Long(itemFrom),new HashSet<>());
        }
        coocurence.get(itemFrom).add(itemTo);
    }
}
