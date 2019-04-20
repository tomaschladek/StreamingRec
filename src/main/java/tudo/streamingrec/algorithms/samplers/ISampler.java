package tudo.streamingrec.algorithms.samplers;

import java.util.List;

public interface ISampler {
    Long add(final List<Long> collection, Long item);
    ISampler copy();
}
