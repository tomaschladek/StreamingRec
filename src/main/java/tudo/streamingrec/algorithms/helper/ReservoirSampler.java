package tudo.streamingrec.algorithms.helper;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class ReservoirSampler<T> {
    final Random rand = new SecureRandom();
    final int reservoirSize;
    int offset = 0;

    public ReservoirSampler(int size) {
        this.reservoirSize = size;

    }

    public void add(final List<T> collection, T item) {
        if (collection.size() < reservoirSize) {
            collection.add(item);
        }
        else {
            int replaceInIndex = (int) (rand.nextDouble() * (reservoirSize + (offset++) + 1));
            if (replaceInIndex < reservoirSize) {
                collection.set(replaceInIndex, item);
            }
        }
    }

    public T get(final List<T> collection)
    {
        return collection.get(rand.nextInt(collection.size()));
    }
}
