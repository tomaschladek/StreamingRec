package testing.streamingrec.algorithms.heuristics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.heuristics.RandomHeuristic;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RandomHeuristicTest extends AbstractHeuristicTest {
    @BeforeEach
    void setUp(){
        this.heuristic = new RandomHeuristic();
    }

    @Test
    void forbiddenItems() {
        this.items = new ArrayList<>();
        items.add(15L);
        this.forbidden = new HashSet<>();
        forbidden.add(15L);
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
        items.add(11L);
        items.add(12L);
        items.add(13L);
        items.add(14L);
        items.add(15L);
        this.forbidden = new HashSet<>();
        forbidden.add(15L);
        for (int index = 0; index < 50; index++)
            assertNotNull(heuristic.get(items,forbidden));
    }
}