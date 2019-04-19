package tudo.streamingrec.algorithms.heuristics;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RecentHeuristic implements IHeuristic {
    private Deque<Long> chain;

    public RecentHeuristic() {
        this.chain = new LinkedList<>();
    }

    @Override
    public Long get(List<Long> items, Set<Long> forbidden) {
        for (Long item : chain) {
            if (items.contains(item) && !forbidden.contains(item))
                return item;
        }
        return null;
    }

    @Override
    public void trainAdd(long userId, long itemId) {
        chain.addFirst(itemId);
    }

    @Override
    public void trainRemove(long userId, long itemId) {
        chain.removeLastOccurrence(itemId);
    }
}
