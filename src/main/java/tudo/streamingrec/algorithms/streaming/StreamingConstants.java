package tudo.streamingrec.algorithms.streaming;

public class StreamingConstants {

    public static final String MODE = "mode";

    public static final String DATA_FRAME_MODE_TIME = "time";
    public static final String DATA_FRAME_MODE_COUNT = "count";
    public static final String DATA_FRAME_PAR_FRAMES = "frames";
    public static final String DATA_FRAME_PAR_SIZE = "size";
    public static final String DATA_FRAME_SINGLE = "single";
    public static final String DATA_FRAME_OVERLAP = "overlap";
    public static final String DATA_FRAME_SEPARATE = "separate";

    public static final String SAMPLER_RESERVOIR = "reservoir";
    public static final String SAMPLER_FLOATING_WINDOW = "floating";
    public static final String SAMPLER_WINDOW = "list";
    public static final String SAMPLER_MODE_FIX = "fixed";
    public static final String SAMPLER_MODE_DYNAMIC = "dynamic";
    public static final String SAMPLER_PAR_SIZE = "size";
    public static final String SAMPLER_PAR_OFFSET = "offset";

    public static final String FILTER_MODE_USER_CACHE = "userCache";
    public static final String FILTER_MODE_FLAG = "flag";
    public static final String FILTER_MODE_COOCURENCE = "coocurence";
    public static final String FILTER_PAR_EXPONENT = "exponent";
    public static final String FILTER_PAR_SIZE = "size";
    public static final String FILTER_PAR_EXPIRATION_TIME = "expirationTime";

    public static final String HEURISTIC_MODE_RANDOM = "random";
    public static final String HEURISTIC_MODE_ITERATOR = "iterator";
    public static final String HEURISTIC_MODE_POPULAR = "popular";
    public static final String HEURISTIC_MODE_RECENT = "recent";
}