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
        this.filter = new CoocurentFilter();
    }

    @ParameterizedTest
    @CsvSource({ "-1,1", "20,2","21,1","30,2" })
    void extendFilter(int itemFrom, int allowedSize) {
        List<Item> items = createItems();
        Set<Long> forbidden = createForbidden();
        List<Long> allowed = createAllowed();
        if (itemFrom != -1)
            filter.trainFromRecommendation(1,20,30);
        filter.extendFilter(1,itemFrom,forbidden,allowed,items);

        assertEquals(1,forbidden.size());
        assertEquals(3,items.size());
        assertEquals(allowedSize,allowed.size());
    }
}