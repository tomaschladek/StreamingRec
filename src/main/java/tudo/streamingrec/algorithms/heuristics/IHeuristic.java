package tudo.streamingrec.algorithms.heuristics;

import java.util.List;
import java.util.Set;

public interface IHeuristic {
    Long get(List<Long> items, Set<Long> forbidden);
    void trainAdd(long userId, long itemId);
    void trainRemove(long userId, long itemId);
    IHeuristic copy();
}
