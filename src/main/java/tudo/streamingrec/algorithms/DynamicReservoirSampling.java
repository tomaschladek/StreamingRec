package tudo.streamingrec.algorithms;

import org.apache.commons.lang3.time.DateUtils;
import tudo.streamingrec.algorithms.helper.ReservoirSamplerDynamic;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DynamicReservoirSampling extends AbstractSampling {

    private int timeFrame = 30;
    private Date frameThresholds = new Date(0,0,0,0,0,0);

    @Override
    protected void trainInternal(List<Item> items, List<ClickData> clickData) {
        //if new items arrive, add their ids to the set of item ids
        super.trainInternal(items,clickData);
        Date timestamp = null;
        if (areClicksUsed)
        {
            for (ClickData data : clickData)
            {
                timestamp = data.click.timestamp;
            }
        }
        else{
            for (Item item : items) {
                timestamp = item.updatedAt;
            }
        }
        if (timestamp != null
                && frameThresholds.before(timestamp))
        {
            frameThresholds = DateUtils.addMinutes(timestamp,timeFrame);
            AssignReservoir();
            sampleTesting = sampleTraining;
            sampleTraining = new ArrayList<>(reservoirSize);
        }
    }

    @Override
    protected void AssignReservoir() {
        this.sampler = new ReservoirSamplerDynamic<>(reservoirSize);
    }


    /**
     * Defines the size of the reservoir
     * @param reservoirSize -
     */
    public void setReservoirSize(int reservoirSize) {
        this.reservoirSize = reservoirSize;
        sampleTraining = new ArrayList<>(reservoirSize);
        sampleTesting = new ArrayList<>(reservoirSize);
        AssignReservoir();
    }

    /**
     * Defines the size of the reservoir
     * @param timeFrame -
     */
    public void setTimeFrame(int timeFrame) {
        this.timeFrame = timeFrame;
    }
}
