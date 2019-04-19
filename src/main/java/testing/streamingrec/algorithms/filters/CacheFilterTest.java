package testing.streamingrec.algorithms.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tudo.streamingrec.algorithms.filters.CacheFilter;
import tudo.streamingrec.algorithms.helper.UserCache;
import tudo.streamingrec.data.Item;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CacheFilterTest extends AbstractFilterTest {

    @BeforeEach
    void setUp() {
        this.filter = new CacheFilter(new UserCache(2,5,1));
    }

    @ParameterizedTest
    @CsvSource({ "-1,1", "20,2","21,2","10,2","40,1" })
    void extendFilter(int itemId, int forbiddenSize) {
        List<Item> items = createItems();
        Set<Long> forbidden = createForbidden();
        Set<Long> allowed = createAllowed();
        if (itemId != -1)
            filter.train(1,itemId);
        filter.extendFilter(1,20,forbidden,allowed,items);

        assertEquals(forbiddenSize,forbidden.size());
        assertEquals(3,items.size());
        assertEquals(1,allowed.size());
    }
}