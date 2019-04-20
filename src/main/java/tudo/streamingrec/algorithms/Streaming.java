package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import tudo.streamingrec.algorithms.streaming.StreamingBuilder;
import tudo.streamingrec.algorithms.streaming.StreamingManager;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.List;

public class Streaming extends Algorithm {

    private StreamingManager executor;
    private StreamingBuilder builder = new StreamingBuilder();

    @Override
    public void initialize() {
        super.initialize();
        executor = builder.construct();
    }

    @Override
    protected void trainInternal(List<Item> items, List<ClickData> transactions) {

        if (items != null)
            for (Item item : items) {
                executor.addArticle(item);
                if (builder.isItem) {
                    executor.trainArticle(item.id, item.updatedAt);
                }
            }

        if (transactions != null)
            if (builder.isTransaction)
            {
                for (ClickData transaction : transactions) {
                    if (transaction.click.userId != 0 || builder.areAnonymousAllowed)
                        executor.trainTransaction(transaction.click.item.id,transaction.click.userId,transaction.click.timestamp);
                }
            }
    }

    @Override
    protected LongArrayList recommendInternal(ClickData clickData) {
        long recommendedValue = executor.recommend(clickData.click.userId,clickData.click.item.id, clickData.click.timestamp);
        return LongArrayList.wrap(new long[]{recommendedValue});
    }

    /**
     * Defines the size of the reservoir
     * @param dataFrame -
     */
    public void setDataFrame(String dataFrame) {
        builder.addDataFrame(dataFrame);
    }

    /**
     * Defines the size of the reservoir
     * @param sampler -
     */
    public void setSampler(String sampler) {
        builder.addSampler(sampler);
    }

    /**
     * Defines the size of the reservoir
     * @param cache -
     */
    public void setCache(String cache) {
        builder.addFilter(cache);
    }

    /**
     * Defines the size of the reservoir
     * @param flag -
     */
    public void setFlag(String flag) {
        setCache(flag);
    }

    /**
     * Defines the size of the reservoir
     * @param coocurence -
     */
    public void setCoocurence(String coocurence) {
        setCache(coocurence);
    }

    /**
     * Defines the size of the reservoir
     * @param heuristic -
     */
    public void setHeuristic(String heuristic) {
        builder.addHeuritic(heuristic);
    }

    /**
     * Defines the size of the reservoir
     * @param areAnonymousAllowed -
     */
    public void setAreAnonymousAllowed(boolean areAnonymousAllowed) {
        builder.areAnonymousAllowed = areAnonymousAllowed;
    }

    /**
     * Defines the size of the reservoir
     * @param isItem -
     */
    public void setIsItem(boolean isItem) {
        builder.isItem = isItem;
    }

    /**
     * Defines the size of the reservoir
     * @param isTransaction -
     */
    public void setIsTransaction(boolean isTransaction) {
        builder.isTransaction = isTransaction;
    }
}
