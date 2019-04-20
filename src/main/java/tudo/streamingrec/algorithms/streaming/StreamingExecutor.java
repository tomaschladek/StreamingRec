package tudo.streamingrec.algorithms.streaming;

import tudo.streamingrec.algorithms.filters.IFilter;
import tudo.streamingrec.algorithms.heuristics.IHeuristic;
import tudo.streamingrec.algorithms.samplers.ISampler;
import tudo.streamingrec.data.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StreamingExecutor implements IStreamingExecutor{
    public IHeuristic heuristic;
    public ISampler sampler;
    public List<Long> collection;

    public StreamingExecutor(ISampler sampler, IHeuristic heuristic) {
        this.heuristic = heuristic;
        this.sampler = sampler;
        this.collection = new ArrayList<>();
    }

    public Long recommend(long userId, long itemId, List<Item> items, List<IFilter> filters) {
        List<Long> included = new ArrayList<>(collection);
        Set<Long> excluded = new HashSet<>();
        excluded.add(itemId);
        for (IFilter filter : filters)
        {
            filter.extendFilter(userId,itemId,excluded,included,items);
        }
        return heuristic.get(included,excluded);
    }

    public void train(long itemId, long userId) {
        Long removed = sampler.add(collection,itemId);
        heuristic.trainAdd(0,itemId);
        if (removed != null) {
            heuristic.trainRemove(0, itemId);
        }
    }

    public StreamingExecutor copy() {
        ISampler newSampler = sampler.copy();
        IHeuristic newHeuristic = heuristic.copy();
        return new StreamingExecutor(newSampler, newHeuristic);
    }

    @Override
    public List<Long> getCollection() {
        return collection;
    }
}
