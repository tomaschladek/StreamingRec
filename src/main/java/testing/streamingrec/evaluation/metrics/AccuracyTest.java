package testing.streamingrec.evaluation.metrics;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tudo.streamingrec.evaluation.metrics.Accuracy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccuracyTest extends AbstractMetricTest {



    @BeforeEach
    void setUp() {
        this.metric = new Accuracy();
    }

    @Test
    void emptyRecommendations()
    {
        metric.evaluate(createTransaction(1,1),new LongArrayList(),new LongOpenHashSet(new long[]{1,2,3,4}));
        assertEquals(0, metric.getResults());
    }

    @Test
    void noSession()
    {
        metric.evaluate(createTransaction(1,1),new LongArrayList(new long[]{1}),new LongOpenHashSet());
        assertTrue(Double.isNaN(metric.getResults()));
    }

    @ParameterizedTest
    @CsvSource({"1,1,1",
            "2,1,1",
            "3,1,1",
            "4,1,1",
            "5,0,1"
    })
    void evaluate(long recommendedValue, double expectedValue, long userId) {
        metric.evaluate(createTransaction(userId,1),new LongArrayList(new long[]{60,recommendedValue}),new LongOpenHashSet(new long[]{1,2,3,4}));
        assertEquals(expectedValue, metric.getResults());
    }

    @ParameterizedTest
    @CsvSource({
            "1,0,0",
            "2,0,0",
            "3,0,0",
            "4,0,0",
            "5,0,0"
    })
    void evaluateAnonyms(long recommendedValue, double expectedValue, long userId) {
        metric.evaluate(createTransaction(userId,1),new LongArrayList(new long[]{recommendedValue}),new LongOpenHashSet(new long[]{1,2,3,4}));
        assertTrue(Double.isNaN(metric.getResults()));
    }

    @ParameterizedTest
    @CsvSource({"1,1,0.5",
            "2,1,1",
            "1,2,0.5",
            "5,6,0",
            "55,1,0.5",
            "1,55,0.5"
    })
    void evaluateMultiple(long recommendedValue1,long recommendedValue2, double expectedValue) {
        metric.evaluate(createTransaction(1,55),new LongArrayList(new long[]{60,recommendedValue1}),new LongOpenHashSet(new long[]{1,2,3,4}));
        metric.evaluate(createTransaction(1,1),new LongArrayList(new long[]{60,recommendedValue2}),new LongOpenHashSet(new long[]{1,3,4}));
        assertEquals(expectedValue, metric.getResults());
    }

}