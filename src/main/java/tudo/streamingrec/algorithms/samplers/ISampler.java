package tudo.streamingrec.algorithms.samplers;

import java.util.List;

public interface ISampler {
    void add(final List<Long> collection, Long item);
}
