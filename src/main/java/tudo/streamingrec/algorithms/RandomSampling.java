package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.apache.commons.lang3.time.DateUtils;
import tudo.streamingrec.algorithms.helper.ReservoirSampler;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RandomSampling extends Algorithm {
    //An unsorted set of all item ids
    private LongOpenHashSet items = new LongOpenHashSet();
    protected ReservoirSampler<Long> sampler = new ReservoirSampler<>(1);
    protected boolean areClicksUsed = true;
    private List<Long> sampleTraining = new ArrayList<>();
    private List<Long> sampleTesting = new ArrayList<>();
    private int timeFrame = 30;
    private int reservoirSize = 30;
    private Date frameThresholds = new Date(0,0,0,0,0,0);

    @Override
    protected void trainInternal(List<Item> items, List<ClickData> clickData) {
        //if new items arrive, add their ids to the set of item ids
        Date timestamp = null;
        if (areClicksUsed)
        {
            for (ClickData data : clickData)
            {
                sampler.add(sampleTraining,data.click.item.id);
                timestamp = data.click.timestamp;
            }
        }
        else{
            for (Item item : items) {
                sampler.add(sampleTraining,item.id);
                timestamp = item.updatedAt;
            }
        }
        if (timestamp != null
                && frameThresholds.before(timestamp))
        {
            frameThresholds = DateUtils.addMinutes(timestamp,timeFrame);
            sampleTesting = sampleTraining;
            this.sampler = new ReservoirSampler<>(reservoirSize);
            sampleTraining = new ArrayList<>(reservoirSize);
        }
    }

    @Override
    public LongArrayList recommendInternal(ClickData clickData) {
        LongArrayList list = new LongArrayList(1);
        list.add(sampler.get(sampleTesting).longValue());
        return list;
    }


    /**
     * Defines the size of the reservoir
     * @param reservoirSize -
     */
    public void setReservoirSize(int reservoirSize) {
        this.reservoirSize = reservoirSize;
        this.sampler = new ReservoirSampler<>(reservoirSize);
        sampleTraining = new ArrayList<>(reservoirSize);
        sampleTesting = new ArrayList<>(reservoirSize);
    }

    /**
     * Defines the size of the reservoir
     * @param areClicksUsed -
     */
    public void setAreClicksUsed(boolean areClicksUsed) {
        this.areClicksUsed = areClicksUsed;
    }

    /**
     * Defines the size of the reservoir
     * @param timeFrame -
     */
    public void setTimeFrame(int timeFrame) {
        this.timeFrame = timeFrame;
    }
}
