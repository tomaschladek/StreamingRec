package testing.streamingrec.algorithms.filters;

import tudo.streamingrec.algorithms.filters.IFilter;
import tudo.streamingrec.data.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractFilterTest {

    protected IFilter filter;

    List<Item> createItems() {
        List<Item> items = new ArrayList<>();
        items.add(createItem(10L, 0));
        items.add(createItem(11L, 8));
        items.add(createItem(12L, 0));
        return items;
    }

    Set<Long> createForbidden() {
        Set<Long> forbidden = new HashSet<>();
        forbidden.add(40L);
        return forbidden;
    }

    List<Long> createAllowed() {
        List<Long> allowed = new ArrayList<>();
        allowed.add(60L);
        return allowed;
    }

    Item createItem(long id, int flag) {
        Item item1 = new Item();
        item1.id = id;
        item1.flag = flag;
        return item1;
    }
}
