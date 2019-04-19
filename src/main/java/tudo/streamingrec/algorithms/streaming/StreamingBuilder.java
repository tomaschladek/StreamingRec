package tudo.streamingrec.algorithms.streaming;

import tudo.streamingrec.algorithms.dataFrames.*;
import tudo.streamingrec.algorithms.filters.CacheFilter;
import tudo.streamingrec.algorithms.filters.CoocurentFilter;
import tudo.streamingrec.algorithms.filters.FlagFilter;
import tudo.streamingrec.algorithms.filters.IFilter;
import tudo.streamingrec.algorithms.helper.UserCache;
import tudo.streamingrec.algorithms.heuristics.*;
import tudo.streamingrec.algorithms.samplers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamingBuilder {

    private final String MODE = "mode";

    private final String DATA_FRAME_MODE_TIME = "time";
    private final String DATA_FRAME_MODE_COUNT = "count";
    private final String DATA_FRAME_PAR_FRAMES = "frames";
    private final String DATA_FRAME_PAR_SIZE = "size";
    private final String DATA_FRAME_SINGLE = "single";
    private final String DATA_FRAME_OVERLAP = "overlap";
    private final String DATA_FRAME_SEPARATE = "separate";

    private final String SAMPLER_RESERVOIR = "reservoir";
    private final String SAMPLER_FLOATING_WINDOW = "floating";
    private final String SAMPLER_WINDOW = "fixed";
    private final String SAMPLER_MODE_FIX = "fixed";
    private final String SAMPLER_MODE_DYNAMIC = "dynamic";
    private final String SAMPLER_PAR_SIZE = "size";
    private final String SAMPLER_PAR_OFFSET = "offset";

    private final String FILTER_MODE_USER_CACHE = "userCache";
    private final String FILTER_MODE_FLAG = "flag";
    private final String FILTER_MODE_COOCURENCE = "coocurence";
    private final String FILTER_PAR_EXPONENT = "exponent";
    private final String FILTER_PAR_SIZE = "size";
    private final String FILTER_PAR_EXPIRATION_TIME = "expirationTime";

    private final String HEURISTIC_MODE_RANDOM = "random";
    private final String HEURISTIC_MODE_ITERATOR = "iterator";
    private final String HEURISTIC_MODE_POPULAR = "popular";
    private final String HEURISTIC_MODE_RECENT = "recent";

    private IDataFrame dataFrame;
    private IHeuristic heuristic;
    private ISampler sampler;
    private List<IFilter> filters = new ArrayList<>();
    public boolean isItem;
    public boolean isTransaction;
    public boolean areAnonymousAllowed;

    public void addDataFrame(String text){
        addDataFrame(getMode(text),getParameters(text));
    }

    private String getMode(String text) {
        return text.split("\\|")[0].trim();
    }

    private Map<String,String> getParameters(String text) {
        String[] splits = text.split("\\|");
        if (splits.length == 0) return new HashMap<>();

        return parseParameters(splits[1]);
    }

    private void addDataFrame(String mode, Map<String,String> pairs)
    {

        switch (mode){
            case DATA_FRAME_SINGLE:
                dataFrame = new SingleDataFrame();
                break;
            case DATA_FRAME_OVERLAP:
                addDataFrameOverlap(pairs);
                break;
            case DATA_FRAME_SEPARATE:
                addDataFrameSeparate(pairs);
                break;
        }
    }

    private void addDataFrameSeparate(Map<String, String> pairs) {
        dataFrame = new SeparateCountDataFrame(getIntArray(pairs,DATA_FRAME_PAR_FRAMES));
    }

    private void addDataFrameOverlap(Map<String, String> pairs) {
        if (pairs.containsKey(MODE)){
            int[] frames = getIntArray(pairs,DATA_FRAME_PAR_FRAMES);
            int trainingTime = getInt(pairs,DATA_FRAME_PAR_SIZE);

            if (pairs.get(MODE).equals(DATA_FRAME_MODE_TIME)) {
                dataFrame = new OverlappingTimingDataFrame(frames,trainingTime);
            }
            if (pairs.get(MODE).equals(DATA_FRAME_MODE_COUNT)) {
                dataFrame = new OverlappingCountDataFrame(frames,trainingTime);
            }
        }
    }

    private int getInt(Map<String, String> pairs, String value) {
        return Integer.parseInt(pairs.get(value));
    }

    private int[] getIntArray(Map<String, String> pairs, String value) {
        String[] frameValues = pairs.get(value).split(",");
        int[] frames = new int[frameValues.length];
        for (int index = 0; index < frameValues.length; index++) {
            frames[index] = Integer.parseInt(frameValues[index]);
        }
        return frames;
    }

    private Map<String, String> parseParameters(String parametrs) {
        Map<String,String> map = new HashMap<>();
        String[] pairs = parametrs.split(";");
        for (String pair : pairs) {
            String key = pair.split(":")[0].trim();
            String value = pair.split(":")[1].trim();
            map.put(key,value);
        }
        return map;
    }

    public void addSampler(String text){
        addSampler(getMode(text),getParameters(text));
    }
    private void addSampler(String mode, Map<String,String> pairs)
    {
        switch (mode){
            case SAMPLER_WINDOW:
                sampler = new WindowSampler();
                break;
            case SAMPLER_FLOATING_WINDOW:
                sampler = new FloatingWindowSampler(getInt(pairs,SAMPLER_PAR_SIZE));
                break;
            case SAMPLER_RESERVOIR:
                addSamplerReservoir(pairs);
                break;
        }
    }

    private void addSamplerReservoir(Map<String, String> pairs) {
        if (pairs.containsKey(MODE)){
            int size = getInt(pairs,SAMPLER_PAR_SIZE);
            if (pairs.get(MODE).equals(SAMPLER_MODE_FIX)) {
                int offset = getInt(pairs,SAMPLER_PAR_OFFSET);
                sampler = new FixedReservoirSampler(size, offset);
            }
            if (pairs.get(MODE).equals(SAMPLER_MODE_DYNAMIC)) {
                sampler = new DynamicReservoirSampler(size);
            }
        }
    }

    public void addFilter(String text)
    {
        addFilter(getMode(text),getParameters(text));
    }

    private void addFilter(String mode, Map<String,String> pairs)
    {
        switch (mode){
            case FILTER_MODE_COOCURENCE:
                filters.add(new CoocurentFilter());
                break;
            case FILTER_MODE_FLAG:
                filters.add(new FlagFilter());
                break;
            case FILTER_MODE_USER_CACHE:
                int exponent = getInt(pairs,FILTER_PAR_EXPONENT);
                int expirationTime = getInt(pairs,FILTER_PAR_EXPIRATION_TIME);
                int size = getInt(pairs,FILTER_PAR_SIZE);
                filters.add(new CacheFilter(new UserCache(exponent,expirationTime,size)));
                break;
        }
    }

    public void addHeuritic(String mode)
    {
        switch (getMode(mode)){
            case HEURISTIC_MODE_RANDOM:
                heuristic = new RandomHeuristic();
                break;
            case HEURISTIC_MODE_ITERATOR:
                heuristic = new IteratorHeuristic();
                break;
            case HEURISTIC_MODE_POPULAR:
                heuristic = new PopularHeuristic();
                break;
            case HEURISTIC_MODE_RECENT:
                heuristic = new RecentHeuristic();
                break;
        }
    }

    public StreamingExecutor construct()
    {
        return new StreamingExecutor(dataFrame,sampler,filters,heuristic);
    }

}
