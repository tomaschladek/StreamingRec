package tudo.streamingrec.algorithms.samplers;

import java.util.List;

public class FloatingWindowSampler implements ISampler {

    private int size;

    public FloatingWindowSampler(int size) {
        this.size = size;
    }

    @Override
    public Long add(List<Long> collection, Long item) {
        Long removedValue = getRemovedValue(collection);
        tryAppend(collection);
        shiftCollection(collection, item);
        return removedValue;
    }

    @Override
    public ISampler copy() {
        return new FloatingWindowSampler(size);
    }

    protected void shiftCollection(List<Long> collection, Long item) {
        for (int index = collection.size()-2; index >= 0; index--) {
            collection.set(index+1,collection.get(index));
        }
        if (collection.isEmpty()){
            collection.add(item);
        }
        else
        {
            collection.set(0,item);
        }
    }

    protected Long getRemovedValue(List<Long> collection) {
        Long removedValue = null;
        if (collection.size() == size)
        {
            removedValue = collection.get(size-1);
        }
        return removedValue;
    }

    protected void tryAppend(List<Long> collection) {
        if (collection.size() < size
                && !collection.isEmpty()){
            collection.add(collection.get(collection.size()-1));
        }
    }
}
