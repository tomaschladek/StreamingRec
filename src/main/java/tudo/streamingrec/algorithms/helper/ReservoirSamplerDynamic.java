package tudo.streamingrec.algorithms.helper;

public class ReservoirSamplerDynamic<T> extends AbstractReservoirSampler<T> {
    int offset = 0;

    public ReservoirSamplerDynamic(int size) {
        super(size);
    }

    @Override
    protected int getReplaceInIndex() {
        return (int) (rand.nextDouble() * (reservoirSize + (offset++) + 1));
    }
}
