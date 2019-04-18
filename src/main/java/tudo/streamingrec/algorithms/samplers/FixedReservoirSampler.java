package tudo.streamingrec.algorithms.samplers;

public class FixedReservoirSampler extends AbstractReservoirSampler {

    int offset;

    public FixedReservoirSampler(int size, int offset) {
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
