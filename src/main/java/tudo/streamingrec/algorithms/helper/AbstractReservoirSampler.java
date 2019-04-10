package tudo.streamingrec.algorithms.helper;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public abstract class AbstractReservoirSampler<T> {
    final Random rand = new SecureRandom();
    final int reservoirSize;

    public AbstractReservoirSampler(int size) {
        this.reservoirSize = size;

    }

    public void add(final List<T> collection, T item) {
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

    public T get(final List<T> collection)
    {
        return collection.get(rand.nextInt(collection.size()));
    }
}
