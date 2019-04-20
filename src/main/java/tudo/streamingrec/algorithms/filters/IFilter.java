package tudo.streamingrec.algorithms.filters;

import tudo.streamingrec.data.Item;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IFilter {
    void extendFilter(long userId, long itemFrom, Set<Long> excluded, List<Long> included, List<Item> items);
    void train(long userId, long itemFrom, Date timestamp);
    void trainFromRecommendation(long userId, long itemFrom, long itemTo);

    IFilter copy();
}
