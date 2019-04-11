package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.apache.commons.lang3.time.DateUtils;
import tudo.streamingrec.algorithms.helper.AbstractReservoirSampler;
import tudo.streamingrec.algorithms.helper.ReservoirSamplerDynamic;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractSampling extends Algorithm {

    private int timeFrameIndex = 0;
    protected int[] timeFrame = new int[]{};
    private Date frameThresholds = new Date(0,0,0,0,0,0);
    protected AbstractReservoirSampler<Long> sampler = new ReservoirSamplerDynamic<>(1);
    protected boolean areClicksUsed = true;
    protected List<Long> sampleTraining = new ArrayList<>();
    protected List<Long> sampleTesting = new ArrayList<>();
    protected int reservoirSize = 30;

    @Override
    protected void trainInternal(List<Item> items, List<ClickData> clickData) {
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
        if (timeFrame.length > 0
                && timestamp != null
                && frameThresholds.before(timestamp))
        {
            frameThresholds = DateUtils.addMinutes(timestamp,timeFrame[timeFrameIndex]);
            timeFrameIndex = (timeFrameIndex + 1) % timeFrame.length;
            AssignReservoir();
            sampleTesting = sampleTraining;
            sampleTraining = new ArrayList<>(reservoirSize);
        }
    }

    protected abstract void AssignReservoir();

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
        if (timeFrame.length == 0)
        {
            sampleTraining = sampleTesting = new ArrayList<>(reservoirSize);
        }
        else{
            sampleTraining = new ArrayList<>(reservoirSize);
            sampleTesting = new ArrayList<>(reservoirSize);
        }
        AssignReservoir();
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
    public void setTimeFrame(String timeFrame) {
        String[] frames = timeFrame.split(",");
        this.timeFrame = new int[frames.length];
        for (int index = 0; index < frames.length; index++) {
            this.timeFrame[index] = Integer.parseInt(frames[index]);
        }
    }
}
