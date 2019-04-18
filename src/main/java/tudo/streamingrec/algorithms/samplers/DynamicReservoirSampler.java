package tudo.streamingrec.algorithms.samplers;

public class DynamicReservoirSampler extends AbstractReservoirSampler {
    int offset = 0;

    public DynamicReservoirSampler(int size) {
        super(size);
    }

    @Override
    protected int getReplaceInIndex() {
        return (int) (rand.nextDouble() * (reservoirSize + (offset++) + 1));
    }
}
