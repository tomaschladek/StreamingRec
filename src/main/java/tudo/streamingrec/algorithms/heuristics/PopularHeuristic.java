package tudo.streamingrec.algorithms.heuristics;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import tudo.streamingrec.algorithms.dtos.ItemCounterDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PopularHeuristic implements IHeuristic {
    private Long2IntOpenHashMap clickCounter;
    private ItemCounterDto first;

    public PopularHeuristic() {
        first = new ItemCounterDto(0,0);
        clickCounter = new Long2IntOpenHashMap();
    }

    @Override
    public Long get(List<Long> items, Set<Long> forbidden) {
        if (!forbidden.contains(first.itemId) && items.contains(first.itemId))
            return first.itemId;

        return getGreatestNotForbiddenItem(items,forbidden);
    }

    private Long getGreatestNotForbiddenItem(List<Long> items, Set<Long> forbidden) {
        Long maxKey = null;
        for (Long key: items) {
            if (clickCounter.containsKey(key.longValue())
                && (maxKey == null
                    || clickCounter.get(key.longValue()) > clickCounter.get(maxKey.longValue()))
                && !forbidden.contains(key))
            {
                maxKey = key;
            }
        }
        return maxKey;
    }

    @Override
    public void trainAdd(long userId, long itemId) {
        clickCounter.addTo(itemId, 1);
        if (clickCounter.get(itemId) > first.count)
        {
            first.itemId = itemId;
            first.count = clickCounter.get(itemId);
        }
    }

    @Override
    public void trainRemove(long userId, long itemId) {
        clickCounter.addTo(itemId, -1);
        if (clickCounter.get(itemId) == 0){
            clickCounter.remove(itemId);
        }
        if (first.itemId == itemId){
            first.itemId = getGreatestNotForbiddenItem(new ArrayList<>(clickCounter.keySet()),new HashSet<>());
            first.count = first.itemId == null
                ? 0
                : clickCounter.get(first.itemId.longValue());
        }
    }

    @Override
    public IHeuristic copy() {
        return new PopularHeuristic();
    }
}
