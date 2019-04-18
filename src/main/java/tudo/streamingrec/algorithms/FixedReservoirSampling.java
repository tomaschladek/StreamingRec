package tudo.streamingrec.algorithms;

import tudo.streamingrec.algorithms.samplers.FixedReservoirSampler;

public class FixedReservoirSampling extends AbstractSampling {

    protected Integer fixedOffset = 1;

    @Override
    protected void assignReservoir() {
        this.sampler = new FixedReservoirSampler(reservoirSize,fixedOffset.intValue());
    }

    /**
     * Defines probability for replacing item
     * @param fixedOffset -
     */
    public void setFixedOffset(int fixedOffset) {
        this.fixedOffset = fixedOffset;
        assignReservoir();
    }
}
