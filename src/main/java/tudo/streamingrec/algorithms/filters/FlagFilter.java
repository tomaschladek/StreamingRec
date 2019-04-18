package tudo.streamingrec.algorithms.filters;

import tudo.streamingrec.data.Item;

import java.util.List;
import java.util.Set;

public class FlagFilter implements IFilter {

    @Override
    public void extendFilter(long userId, long itemFrom, Set<Long> forbiddenIds, Set<Long> allowedIds, List<Item> items) {
        for (Item item : items) {
            if (item.flag != 0){
                forbiddenIds.add(item.id);
            }
        }
    }

    @Override
    public void train(long userId, long itemFrom, long itemTo) {

    }
}
