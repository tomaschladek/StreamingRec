package tudo.streamingrec.algorithms.filters;

import tudo.streamingrec.data.Item;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class FlagFilter implements IFilter {

    @Override
    public void extendFilter(long userId, long itemFrom, Set<Long> forbiddenIds, List<Long> allowedIds, List<Item> items) {
        if (forbiddenIds == null) return;
        for (Item item : items) {
            if (item.flag != 0){
                forbiddenIds.add(item.id);
            }
        }
    }

    @Override
    public void train(long userId, long itemFrom, Date timestamp) {

    }

    @Override
    public void trainFromRecommendation(long userId, long itemFrom, long itemTo) {

    }

    @Override
    public IFilter copy() {
        return new FlagFilter();
    }
}
