package tudo.streamingrec.algorithms.helper;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public abstract class AbstractReservoirSampler {
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

    public Long get(final List<Long> collection)
    {
        return collection.get(rand.nextInt(collection.size()));
    }

    public Long get(final List<Long> collection, long sample)
    {
        int start = rand.nextInt(collection.size());
        for (int index = 0; index < collection.size(); index++)
        {
            int itemIndex = start + index;
            if (itemIndex >= collection.size())
            {
                itemIndex -= collection.size();
            }
            if (collection.get(itemIndex).longValue() != sample) return collection.get(itemIndex);
        }
        return get(collection);
    }
}
