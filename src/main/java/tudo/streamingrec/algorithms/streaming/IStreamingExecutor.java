package tudo.streamingrec.algorithms.streaming;

import tudo.streamingrec.algorithms.filters.IFilter;
import tudo.streamingrec.data.Item;

import java.util.List;

public interface IStreamingExecutor {
    Long recommend(long userId, long itemId, List<Item> items, List<IFilter> filters);
    void train(long itemId, long userId);
    IStreamingExecutor copy();
    List<Long> getCollection();
}
