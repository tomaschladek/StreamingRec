package tudo.streamingrec.algorithms.filters;

import tudo.streamingrec.data.Item;

import java.util.List;
import java.util.Set;

public interface IFilter {
    void extendFilter(long userId, long itemFrom, Set<Long> forbiddenIds, Set<Long> allowedIds, List<Item> items);
    void train(long userId, long itemFrom);
    void trainFromRecommendation(long userId, long itemFrom, long itemTo);
}
