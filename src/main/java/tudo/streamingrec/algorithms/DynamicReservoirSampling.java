package tudo.streamingrec.algorithms;

import tudo.streamingrec.algorithms.helper.ReservoirSamplerDynamic;

public class DynamicReservoirSampling extends AbstractSampling {


    @Override
    protected void assignReservoir() {
        this.sampler = new ReservoirSamplerDynamic<>(reservoirSize);
    }

}
