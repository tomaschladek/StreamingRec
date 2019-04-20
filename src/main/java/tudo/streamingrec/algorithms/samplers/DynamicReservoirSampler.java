package tudo.streamingrec.algorithms.samplers;

public class DynamicReservoirSampler extends AbstractReservoirSampler {
    int offset = 0;

    public DynamicReservoirSampler(int reservoirSize) {
        super(reservoirSize);
    }

    @Override
    protected int getReplaceInIndex() {
        return (int) (rand.nextDouble() * (reservoirSize + (offset++) + 1));
    }

    @Override
    public ISampler copy() {
        return new DynamicReservoirSampler(reservoirSize);
    }
}
