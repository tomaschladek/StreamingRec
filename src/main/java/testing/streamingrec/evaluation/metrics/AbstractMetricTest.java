package testing.streamingrec.evaluation.metrics;

import tudo.streamingrec.data.Item;
import tudo.streamingrec.data.Transaction;
import tudo.streamingrec.evaluation.metrics.HypothesisTestableMetric;

import java.util.Date;

public class AbstractMetricTest {
    protected HypothesisTestableMetric metric;

    protected Transaction createTransaction(long userId, long itemId) {
        Transaction transaction = new Transaction();
        transaction.timestamp = new Date(0);
        transaction.item = createItem(itemId);
        transaction.userId = userId;
        return transaction;
    }

    protected Item createItem(long itemId) {
        Item item = new Item();
        item.id = itemId;
        return item;
    }
}
