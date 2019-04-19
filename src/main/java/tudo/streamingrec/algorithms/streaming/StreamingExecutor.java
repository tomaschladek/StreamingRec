package tudo.streamingrec.algorithms.streaming;

import tudo.streamingrec.algorithms.dataFrames.IDataFrame;
import tudo.streamingrec.algorithms.filters.IFilter;
import tudo.streamingrec.algorithms.heuristics.IHeuristic;
import tudo.streamingrec.algorithms.samplers.ISampler;
import tudo.streamingrec.data.Item;

import java.util.*;

public class StreamingExecutor {
    private IDataFrame dataFrame;
    private IHeuristic heuristic;
    private ISampler sampler;
    private List<IFilter> filters;
    private List<Item> items;

    public StreamingExecutor(IDataFrame dataFrame, ISampler sampler, List<IFilter> filters, IHeuristic heuristic) {
        this.dataFrame = dataFrame;
        this.heuristic = heuristic;
        this.sampler = sampler;
        this.filters = filters;
        items = new ArrayList<>();
    }

    public Date trainArticle(long itemId, Date updatedAt) {
        return trainTransaction(itemId,0,updatedAt);
    }

    public Date trainTransaction(long itemId, long userId, Date timestamp) {
        for (List<Long> dataset : dataFrame.getTrainingData(timestamp)){
            Long removed = sampler.add(dataset,itemId);
            heuristic.trainAdd(0,itemId);
            if (removed != null) {
                heuristic.trainRemove(0, itemId);
            }
        }
        if (filters != null) {
            for (IFilter filter : filters) {
                filter.train(userId, itemId);
            }
        }
        dataFrame.update(timestamp);
        return timestamp;
    }

    public long recommend(long userId, long itemId) {
        List<Long> included = new ArrayList<>(dataFrame.getTestingData());
        Set<Long> excluded = new HashSet<>();
        excluded.add(itemId);
        for (IFilter filter : filters)
        {
            filter.extendFilter(userId,itemId,excluded,included,items);
        }
        Long recommendedValue = heuristic.get(included,excluded);
        if (recommendedValue == null) return itemId;
        if (filters != null) {
            for (IFilter filter : filters) {
                filter.trainFromRecommendation(userId, itemId, recommendedValue);
            }
        }
        return  recommendedValue;
    }

    public void addArticle(Item item) {
        items.add(item);
    }
}
