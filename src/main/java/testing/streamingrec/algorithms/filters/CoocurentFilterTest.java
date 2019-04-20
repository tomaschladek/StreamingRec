package testing.streamingrec.algorithms.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tudo.streamingrec.algorithms.filters.CoocurentFilter;
import tudo.streamingrec.data.Item;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CoocurentFilterTest extends AbstractFilterTest{

    @BeforeEach
    void setUp() {
        this.filter = new CoocurentFilter(60*24);
    }

    @ParameterizedTest
    @CsvSource({ "10,-1,2","10,20,2","60,-1,2","60,60,2","30,60,2","30,50,1"})
    void extendFilter(int itemFromFilter, int itemFrom, int expectedSize) {
        List<Item> items = createItems();
        Set<Long> excluded = createForbidden();
        List<Long> included = createAllowed();
        included.add(30L);
        if (itemFrom != -1)
            filter.trainFromRecommendation(1,itemFrom,30);
        filter.extendFilter(1,itemFromFilter,excluded,included,items);

        assertEquals(1,excluded.size());
        assertEquals(3,items.size());
        assertEquals(expectedSize,included.size());
    }
}