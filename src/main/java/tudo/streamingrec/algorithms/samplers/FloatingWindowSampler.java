package tudo.streamingrec.algorithms.samplers;

import java.util.List;

public class FloatingWindowSampler implements ISampler {

    private int size;

    public FloatingWindowSampler(int size) {
        this.size = size;
    }

    @Override
    public void add(List<Long> collection, Long item) {
        if (collection.size() < size
                && !collection.isEmpty()){
            collection.add(collection.get(collection.size()-1));
        }
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
}
