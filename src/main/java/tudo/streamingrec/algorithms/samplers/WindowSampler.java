package tudo.streamingrec.algorithms.samplers;

import java.util.List;

public class WindowSampler implements ISampler {

    @Override
    public Long add(List<Long> collection, Long item) {
        collection.add(item);
        return null;
    }

    @Override
    public ISampler copy() {
        return new WindowSampler();
    }
}
