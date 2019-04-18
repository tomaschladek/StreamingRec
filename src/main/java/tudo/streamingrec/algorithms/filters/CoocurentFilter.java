package tudo.streamingrec.algorithms.filters;

import tudo.streamingrec.data.Item;

import java.util.*;

public class CoocurentFilter implements IFilter {

    private Map<Long,Set<Long>> coocurence = new HashMap<>();

    public CoocurentFilter(Map<Long, Set<Long>> coocurence) {
        this.coocurence = coocurence;
    }

    @Override
    public void extendFilter(long userId, long itemFrom, Set<Long> forbiddenIds, Set<Long> allowedIds, List<Item> items) {
        allowedIds.addAll(coocurence.get(itemFrom));
    }

    @Override
    public void train(long userId, long itemFrom, long itemTo) {
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
