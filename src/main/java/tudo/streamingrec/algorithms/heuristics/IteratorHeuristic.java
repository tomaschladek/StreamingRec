package tudo.streamingrec.algorithms.heuristics;

import java.util.List;
import java.util.Set;

public class IteratorHeuristic implements IHeuristic {
    @Override
    public Long get(List<Long> items, Set<Long> forbidden) {
        for (Long item : items) {
            if (!forbidden.contains(item))
                return item;
        }
        return null;
    }

    @Override
    public void trainAdd(long userId, long itemId) {

    }

    @Override
    public void trainRemove(long userId, long itemId) {

    }

    @Override
    public IHeuristic copy() {
        return new IteratorHeuristic();
    }
}
