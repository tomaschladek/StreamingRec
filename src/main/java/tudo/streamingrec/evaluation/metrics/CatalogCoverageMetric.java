package tudo.streamingrec.evaluation.metrics;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.apache.commons.collections4.CollectionUtils;
import tudo.streamingrec.data.Transaction;

import java.util.HashSet;
import java.util.Set;

public class CatalogCoverageMetric extends HypothesisTestableMetric{
    private static final long serialVersionUID = -5868960950896363416L;
    //result storage
    private Set<Long> itemIds = new HashSet<>();
    private Set<Long> recommendedItemIds = new HashSet<>();
    private boolean returnRatio = false;
    @Override
    public void evaluate(Transaction transaction, LongArrayList recommendations,
                         LongOpenHashSet userTransactions) {
        if (recommendations != null)
            for(Long recommendation : recommendations)
                recommendedItemIds.add(recommendation);
        if (userTransactions != null)
            for(Long itemId : userTransactions)
                itemIds.add(itemId);
        if (transaction != null)
            itemIds.add(transaction.item.id);
    }

    @Override
    public double getResults() {
        long matched = CollectionUtils.intersection(itemIds,recommendedItemIds).size();
        long total = itemIds.size();
        if (returnRatio) {
            if (total == 0) {
                return 1;
            }
            return Math.min((double) matched / total, 1);
        }
        return matched;
    }

    @Override
    public DoubleArrayList getDetailedResults() {
        return new DoubleArrayList();
    }

    /**
     * Defines format of return value
     * @param returnRatio - if true then returns ratio
     */
    public void setReturnRatio(boolean returnRatio) {
        this.returnRatio = returnRatio;
    }
}
