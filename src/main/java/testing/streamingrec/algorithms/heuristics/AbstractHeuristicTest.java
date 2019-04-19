package testing.streamingrec.algorithms.heuristics;

import org.junit.jupiter.api.BeforeEach;
import tudo.streamingrec.algorithms.heuristics.IHeuristic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractHeuristicTest {

    IHeuristic heuristic;
    protected ArrayList<Long> items;
    protected Set<Long> forbidden;

    @BeforeEach
    void setUp(){
        this.items = new ArrayList<>();
        items.add(10L);
        items.add(11L);
        items.add(12L);
        items.add(13L);
        items.add(14L);
        this.forbidden = new HashSet<>();
        forbidden.add(15L);
        forbidden.add(16L);
    }
}
