package testing.streamingrec.algorithms.heuristics;

import org.junit.jupiter.api.BeforeEach;
import tudo.streamingrec.algorithms.heuristics.IHeuristic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractHeuristicTest {

    protected IHeuristic heuristic;
    protected ArrayList<Long> items;
    protected Set<Long> forbidden;

    @BeforeEach
    void setUp(){
        this.items = new ArrayList<>();
        items.add(10l);
        items.add(11l);
        items.add(12l);
        items.add(13l);
        items.add(14l);
        this.forbidden = new HashSet<>();
        forbidden.add(15l);
        forbidden.add(16l);
    }
}
