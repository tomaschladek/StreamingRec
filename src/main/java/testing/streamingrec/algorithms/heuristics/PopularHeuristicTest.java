package testing.streamingrec.algorithms.heuristics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudo.streamingrec.algorithms.heuristics.PopularHeuristic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PopularHeuristicTest extends AbstractHeuristicTest{

    @BeforeEach
    void setUp(){
        super.setUp();
        this.heuristic = new PopularHeuristic();
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

        trainAdd(10,11,12,10,11,12);
        trainAdd(11);

        trainRemove(new int[]{11,11,12,11,10,10},new int[]{11,10,10,10,10,12});

        heuristic.trainRemove(1,12);
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
            assertEquals(itemIds[0], heuristic.get(items, forbidden));
        }
    }
}