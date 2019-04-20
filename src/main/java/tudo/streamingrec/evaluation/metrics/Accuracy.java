package tudo.streamingrec.evaluation.metrics;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import tudo.streamingrec.data.Transaction;

import java.util.HashMap;

public class Accuracy extends HypothesisTestableMetric {
    private static final long serialVersionUID = 5757499847554728701L;
    // result storage
    private DoubleArrayList results = new DoubleArrayList();
    private HashMap<Long,LongOpenHashSet> interactionMatrix = new HashMap<>();

    @Override
    public DoubleArrayList getDetailedResults() {
        return results;
    }

    @Override
    public void evaluate(Transaction transaction, LongArrayList recommendations, LongOpenHashSet userTransactions) {
        if (!hasAllAtributes(transaction, userTransactions)) {
            return;
        }

        Long recommendedValue = getFirstRecommendValue(transaction, recommendations, userTransactions);
        if (recommendedValue == null)
        {
            results.add(0);
        }
        else{
            updateInteractionMatrix(transaction, recommendedValue);
            results.add(1);
        }
    }

    protected void updateInteractionMatrix(Transaction transaction, Long recommendedValue) {
        if (!interactionMatrix.containsKey(transaction.userId))
        {
            interactionMatrix.put(transaction.userId,new LongOpenHashSet());
        }
        interactionMatrix.get(transaction.userId).add(recommendedValue);
    }

    protected boolean hasAllAtributes(Transaction transaction, LongOpenHashSet userTransactions) {
        return userTransactions != null
                && !userTransactions.isEmpty()
                && transaction.userId != 0;
    }

    protected Long getFirstRecommendValue(Transaction transaction, LongArrayList recommendations, LongOpenHashSet userTransactions) {
        LongArrayList recommendedValues = new LongArrayList();
        if (!recommendations.isEmpty()) {
            for (long recommendation : recommendations) {
                if (userTransactions.contains(recommendation)
                    && (!interactionMatrix.containsKey(transaction.userId)
                        || (
                            interactionMatrix.containsKey(transaction.userId)
                            && !interactionMatrix.get(transaction.userId).contains(recommendation)))) {
                    recommendedValues.add(recommendation);
                }
            }
        }
        return recommendedValues.isEmpty() ? null : recommendedValues.getLong(0);
    }

    @Override
    public double getResults() {
        //return the average results
        return getAvg(results);
    }
}
