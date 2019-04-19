package testing.streamingrec.algorithms.filters;

import tudo.streamingrec.algorithms.filters.IFilter;
import tudo.streamingrec.data.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractFilterTest {

    protected IFilter filter;

    protected List<Item> createItems() {
        List<Item> items = new ArrayList<>();
        items.add(createItem(10l, 0));
        items.add(createItem(11l, 8));
        items.add(createItem(12l, 0));
        return items;
    }

    protected Set<Long> createForbidden() {
        Set<Long> forbidden = new HashSet<>();
        forbidden.add(40l);
        return forbidden;
    }

    protected Set<Long> createAllowed() {
        Set<Long> allowed = new HashSet<>();
        allowed.add(60l);
        return allowed;
    }

    protected Item createItem(long id, int flag) {
        Item item1 = new Item();
        item1.id = id;
        item1.flag = flag;
        return item1;
    }
}
