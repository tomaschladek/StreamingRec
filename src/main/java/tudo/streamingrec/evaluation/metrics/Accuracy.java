package tudo.streamingrec.evaluation.metrics;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import tudo.streamingrec.data.Transaction;

import java.util.HashMap;

public class Accuracy extends HypothesisTestableMetric {
    private static final long serialVersionUID = 5757499847554728701L;
    // result storage
    private DoubleArrayList results = new DoubleArrayList();
    private HashMap<Long,LongOpenHashSet> uniques = new HashMap<>();
    //the type (Precision or Recall)
    private PrecisionOrRecall.Type type = PrecisionOrRecall.Type.Undefined;

    @Override
    public DoubleArrayList getDetailedResults() {
        return results;
    }

    @Override
    public void evaluate(Transaction transaction, LongArrayList recommendations, LongOpenHashSet userTransactions) {
        //no future unique IDs
        if (userTransactions == null || userTransactions.isEmpty()) {
            return;
        }
        // if the algorithm does not return any recommendations, count it as 0
        if (recommendations.isEmpty()) {
            results.add(0);
            return;
        }

        long recommendedValue = recommendations.getLong(0);
        if (transaction.userId != 0 && uniques.containsKey(transaction.userId) && uniques.get(transaction.userId).contains(recommendedValue))
        {
            results.add(0);
            return;
        }

        if (!uniques.containsKey(transaction.userId))
        {
            uniques.put(transaction.userId,new LongOpenHashSet());
        }
        uniques.get(transaction.userId).add(recommendedValue);

        // calculate the precision
        double result = 0;
        // iterate over relevant items and recommendations to calculate the
        // intersection
        for (LongIterator iterator = userTransactions.iterator(); iterator.hasNext();) {
            if (iterator.nextLong() == recommendedValue) {
                result = 1;
            }
        }

        results.add(result);
    }

    @Override
    public double getResults() {
        //return the average results
        return getAvg(results);
    }
}
