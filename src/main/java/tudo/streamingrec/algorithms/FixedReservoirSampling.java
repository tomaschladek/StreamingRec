package tudo.streamingrec.algorithms;

import tudo.streamingrec.algorithms.helper.ReservoirSamplerFixed;

import java.util.ArrayList;

public class FixedReservoirSampling extends AbstractSampling {

    protected Integer fixedOffset = 1;

    @Override
    protected void AssignReservoir() {
        this.sampler = new ReservoirSamplerFixed<>(reservoirSize,fixedOffset.intValue());
    }

    /**
     * Defines the size of the reservoir
     * @param reservoirSize -
     */
    public void setReservoirSize(int reservoirSize) {
        this.reservoirSize = reservoirSize;
        sampleTraining = sampleTesting = new ArrayList<>(reservoirSize);
        AssignReservoir();
    }

    /**
     * Defines probability for replacing item
     * @param fixedOffset -
     */
    public void setFixedOffset(int fixedOffset) {
        this.fixedOffset = fixedOffset;
        AssignReservoir();
    }
}
