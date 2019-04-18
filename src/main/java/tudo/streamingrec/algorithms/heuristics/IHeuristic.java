package tudo.streamingrec.algorithms.heuristics;

import java.util.List;
import java.util.Set;

public interface IHeuristic {
    Long get(List<Long> items, Set<Long> forbidden);
}
