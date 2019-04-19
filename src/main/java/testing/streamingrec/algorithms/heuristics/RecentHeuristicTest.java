package testing.streamingrec.algorithms.heuristics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.heuristics.RecentHeuristic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RecentHeuristicTest extends AbstractHeuristicTest {

    @BeforeEach
    void setUp(){
        super.setUp();
        this.heuristic = new RecentHeuristic();
    }

    @Test
    void forbiddenItems() {
        heuristic.trainAdd(1, 15);
        assertNull(heuristic.get(items, forbidden), "Forbidden item");
    }

    @Test
    void notAllowed() {
        heuristic.trainAdd(1, 17);
        assertNull(heuristic.get(items, forbidden),"Not in the items");
    }

    @Test
    void get() {
        assertNull(heuristic.get(items,forbidden));
        trainAdd(10,11,12,11);

        trainRemove(new int[]{11,11,12},new int[]{11,12,10});

        heuristic.trainRemove(1,10);
        assertNull(heuristic.get(items,forbidden));
    }

    private void trainRemove(int[] itemIds,int[] expectedValues) {
        for (int index = 0; index < itemIds.length; index++) {
            heuristic.trainRemove(1, itemIds[index]);
            assertEquals(expectedValues[index], heuristic.get(items, forbidden));
        }
    }

    private void trainAdd(int... itemIds) {
        for (int item : itemIds) {
            heuristic.trainAdd(1, item);
            assertEquals(item, heuristic.get(items, forbidden));
        }
    }
}