package tudo.streamingrec.algorithms.samplers;

public class FixedReservoirSampler extends AbstractReservoirSampler {
    int offset;

    public FixedReservoirSampler(int reservoirSize, int offset) {
        super(reservoirSize);
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

    @Override
    public ISampler copy() {
        return new FixedReservoirSampler(reservoirSize,offset);
    }
}
