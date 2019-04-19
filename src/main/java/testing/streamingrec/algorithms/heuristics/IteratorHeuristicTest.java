package testing.streamingrec.algorithms.heuristics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.heuristics.IteratorHeuristic;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IteratorHeuristicTest extends AbstractHeuristicTest {
    @BeforeEach
    void setUp(){
        this.heuristic = new IteratorHeuristic();
    }

    @Test
    void forbiddenItems() {
        this.items = new ArrayList<>();
        items.add(15l);
        this.forbidden = new HashSet<>();
        forbidden.add(15l);
        assertNull(heuristic.get(items, forbidden), "Forbidden item");
    }

    @Test
    void notAllowed() {
        this.items = new ArrayList<>();
        this.forbidden = new HashSet<>();
        assertNull(heuristic.get(items, forbidden),"Not in the items");
    }

    @Test
    void get() {
        this.items = new ArrayList<>();
        items.add(11l);
        items.add(12l);
        items.add(13l);
        items.add(14l);
        items.add(15l);
        this.forbidden = new HashSet<>();
        forbidden.add(15l);
        assertEquals(11l,heuristic.get(items,forbidden));
        items.add(0,10l);
        assertEquals(10l,heuristic.get(items,forbidden));
    }
}