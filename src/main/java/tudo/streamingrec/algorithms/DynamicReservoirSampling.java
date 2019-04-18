package tudo.streamingrec.algorithms;

public class DynamicReservoirSampling extends AbstractSampling {


    @Override
    protected void assignReservoir() {

        this.sampler = new tudo.streamingrec.algorithms.samplers.DynamicReservoirSampler(reservoirSize);
    }

}
