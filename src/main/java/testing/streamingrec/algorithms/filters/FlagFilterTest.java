package testing.streamingrec.algorithms.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tudo.streamingrec.algorithms.filters.FlagFilter;
import tudo.streamingrec.data.Item;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlagFilterTest extends AbstractFilterTest {

    @BeforeEach
    protected void setUp() {
        this.filter = new FlagFilter();
    }

    @ParameterizedTest
    @CsvSource({"11,2","12,3","13,3","40,2",})
    void extendFilter(long itemId, int forbiddenSize) {
        List<Item> items = createItems();
        items.add(createItem(itemId,8));
        Set<Long> forbidden = createForbidden();
        Set<Long> allowed = createAllowed();
        filter.extendFilter(1,2,forbidden,allowed,items);

        assertEquals(forbiddenSize,forbidden.size());
        assertEquals(4,items.size());
        assertEquals(1,allowed.size());
        if (forbiddenSize > 2)
            assertTrue(forbidden.contains(itemId));
        assertTrue(forbidden.contains(11l));
        assertTrue(forbidden.contains(40l));
    }

}