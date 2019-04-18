package tudo.streamingrec.algorithms.samplers;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public abstract class AbstractReservoirSampler implements ISampler {
    final Random rand = new SecureRandom();
    final int reservoirSize;

    public AbstractReservoirSampler(int size) {
        this.reservoirSize = size;
    }

    public void add(final List<Long> collection, Long item) {
        if (collection.size() < reservoirSize) {
            collection.add(item);
        }
        else {
            int replaceInIndex = getReplaceInIndex();
            if (replaceInIndex < reservoirSize) {
                collection.set(replaceInIndex, item);
            }
        }
    }

    protected abstract int getReplaceInIndex();
}
