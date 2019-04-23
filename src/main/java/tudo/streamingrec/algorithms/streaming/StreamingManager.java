package tudo.streamingrec.algorithms.streaming;

import tudo.streamingrec.algorithms.dataFrames.IDataFrame;
import tudo.streamingrec.algorithms.filters.IFilter;
import tudo.streamingrec.data.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StreamingManager {
    private IDataFrame dataFrame;
    private List<Item> items;
    public List<IFilter> filters;

    StreamingManager(IDataFrame dataFrame, List<IFilter> filters) {
        this.dataFrame = dataFrame;
        items = new ArrayList<>();
        this.filters = filters;
    }

    public void trainArticle(long itemId, Date updatedAt) {
        trainTransaction(itemId, 0, updatedAt);
    }

    public void trainTransaction(long itemId, long userId, Date timestamp) {
        for (IStreamingExecutor executor : dataFrame.getTrainingData(timestamp)){
            executor.train(itemId,userId);
        }
        if (filters != null) {
            for (IFilter filter : filters) {
                filter.train(userId, itemId,timestamp);
            }
        }

        dataFrame.update(timestamp);
    }

    public long recommend(long userId, long itemId) {
        IStreamingExecutor executor = dataFrame.getTestingData();
        Long recommendedValue = executor.recommend(userId,itemId,items,filters);
        if (recommendedValue == null) return itemId;

        trainRecommendedValue(userId, itemId, recommendedValue);
        return  recommendedValue;
    }

    private void trainRecommendedValue(long userId, long itemId, Long recommendedValue) {
        if (filters != null) {
            for (IFilter filter : filters) {
                filter.trainFromRecommendation(userId, itemId, recommendedValue);
            }
        }
    }

    public void addArticle(Item item) {
        items.add(item);
    }
}
