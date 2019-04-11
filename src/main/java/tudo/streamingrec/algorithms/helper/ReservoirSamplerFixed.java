package tudo.streamingrec.algorithms.helper;

public class ReservoirSamplerFixed<T> extends AbstractReservoirSampler<T> {

    int offset;

    public ReservoirSamplerFixed(int size, int offset) {
        super(size);
        if (offset < 0)
        {
            offset = 0;
        }
        this.offset = offset;
    }

    @Override
    protected int getReplaceInIndex() {
        return (int) (rand.nextDouble() * (reservoirSize + (offset)));
    }
}