package testing.streamingrec.evaluation.metrics;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tudo.streamingrec.evaluation.metrics.CatalogCoverageMetric;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CatalogCoverageMetricTest extends AbstractMetricTest{

    private CatalogCoverageMetric metric;

    @BeforeEach
    void setUp() {
        this.metric = new CatalogCoverageMetric();
    }

    @ParameterizedTest
    @CsvSource({"true","false"})
    void emptyRecommendations(boolean isRatioOn)
    {
        metric.evaluate(createTransaction(1,1),new LongArrayList(),new LongOpenHashSet(new long[]{1,2,3,4}));
        metric.setReturnRatio(isRatioOn);
        assertEquals(0, metric.getResults());
    }

    @ParameterizedTest
    @CsvSource({"true,1","false,0"})
    void noSession(boolean isRatioOn, double expectedValue)
    {
        metric.setReturnRatio(isRatioOn);
        metric.evaluate(createTransaction(1,1),new LongArrayList(new long[]{1}),new LongOpenHashSet());
        assertEquals(expectedValue,metric.getResults());
    }

    @ParameterizedTest
    @CsvSource({"true,0.75","false,3"})
    void evaluateSubSet(boolean isRatioOn, double expectedValue) {
        metric.setReturnRatio(isRatioOn);
        metric.evaluate(createTransaction(1,1),new LongArrayList(new long[]{1,2,3}),new LongOpenHashSet(new long[]{1,2,3,4}));
        assertEquals(expectedValue, metric.getResults());
    }

    @ParameterizedTest
    @CsvSource({"true,0.75","false,3"})
    void evaluate(boolean isRatioOn, double expectedValue) {
        metric.setReturnRatio(isRatioOn);
        metric.evaluate(createTransaction(1,1),new LongArrayList(new long[]{1}),new LongOpenHashSet(new long[]{1,2,3,4}));
        metric.evaluate(createTransaction(1,1),new LongArrayList(new long[]{2}),new LongOpenHashSet(new long[]{1,2,3,4}));
        metric.evaluate(createTransaction(1,1),new LongArrayList(new long[]{3}),new LongOpenHashSet(new long[]{1,2,3,4}));
        assertEquals(expectedValue, metric.getResults());
    }

    @ParameterizedTest
    @CsvSource({"true","false"})
    void findNoCoverage(boolean isRatioOn) {
        metric.setReturnRatio(isRatioOn);
        metric.evaluate(createTransaction(1,1),new LongArrayList(new long[]{5}),new LongOpenHashSet(new long[]{1,2,3,4}));
        assertEquals(0, metric.getResults());
    }
}