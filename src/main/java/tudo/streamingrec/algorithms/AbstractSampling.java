package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import tudo.streamingrec.algorithms.helper.AbstractReservoirSampler;
import tudo.streamingrec.algorithms.helper.ReservoirSamplerDynamic;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSampling extends Algorithm {

    protected AbstractReservoirSampler<Long> sampler = new ReservoirSamplerDynamic<>(1);
    protected boolean areClicksUsed = true;
    protected List<Long> sampleTraining = new ArrayList<>();
    protected List<Long> sampleTesting = new ArrayList<>();
    protected int reservoirSize = 30;

    @Override
    protected void trainInternal(List<Item> items, List<ClickData> clickData) {
        //if new items arrive, add their ids to the set of item ids
        if (areClicksUsed)
        {
            for (ClickData data : clickData)
            {
                sampler.add(sampleTraining,data.click.item.id);
            }
        }
        else{
            for (Item item : items) {
                sampler.add(sampleTraining,item.id);
            }
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
    public abstract void setReservoirSize(int reservoirSize);

    /**
     * Defines the size of the reservoir
     * @param areClicksUsed -
     */
    public void setAreClicksUsed(boolean areClicksUsed) {
        this.areClicksUsed = areClicksUsed;
    }
}
