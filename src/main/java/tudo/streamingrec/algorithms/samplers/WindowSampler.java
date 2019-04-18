package tudo.streamingrec.algorithms.samplers;

import java.util.List;

public class WindowSampler implements ISampler {

    @Override
    public void add(List<Long> collection, Long item) {
        collection.add(item);
    }
}
