package tudo.streamingrec.algorithms.streaming;

import tudo.streamingrec.algorithms.dataFrames.*;
import tudo.streamingrec.algorithms.filters.CacheFilter;
import tudo.streamingrec.algorithms.filters.CoocurentFilter;
import tudo.streamingrec.algorithms.filters.FlagFilter;
import tudo.streamingrec.algorithms.filters.IFilter;
import tudo.streamingrec.algorithms.heuristics.*;
import tudo.streamingrec.algorithms.samplers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamingBuilder {
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
        if (splits.length <= 1) return new HashMap<>();

        return parseParameters(splits[1]);
    }

    private void addDataFrame(String mode, Map<String,String> pairs)
    {

        switch (mode){
            case StreamingConstants.DATA_FRAME_SINGLE:
                dataFrame = new SingleDataFrame();
                break;
            case StreamingConstants.DATA_FRAME_OVERLAP:
                addDataFrameOverlap(pairs);
                break;
            case StreamingConstants.DATA_FRAME_SEPARATE:
                addDataFrameSeparate(pairs);
                break;
            default:
                throw new IllegalArgumentException("Unknown filter command: " + mode);
        }
    }

    private void addDataFrameSeparate(Map<String, String> pairs) {
        dataFrame = new SeparateCountDataFrame(getIntArray(pairs,StreamingConstants.DATA_FRAME_PAR_FRAMES));
    }

    private void addDataFrameOverlap(Map<String, String> pairs) {
        if (pairs.containsKey(StreamingConstants.MODE)){
            int[] frames = getIntArray(pairs,StreamingConstants.DATA_FRAME_PAR_FRAMES);
            int trainingTime = getInt(pairs,StreamingConstants.DATA_FRAME_PAR_SIZE);

            if (pairs.get(StreamingConstants.MODE).equals(StreamingConstants.DATA_FRAME_MODE_TIME)) {
                dataFrame = new OverlappingTimingDataFrame(frames,trainingTime);
            }
            if (pairs.get(StreamingConstants.MODE).equals(StreamingConstants.DATA_FRAME_MODE_COUNT)) {
                dataFrame = new OverlappingCountDataFrame(frames,trainingTime);
            }
        }
    }

    private int getInt(Map<String, String> pairs, String value) {
        if (!pairs.containsKey(value))
            throw new IllegalArgumentException(value);
        return Integer.parseInt(pairs.get(value));
    }

    private int[] getIntArray(Map<String, String> pairs, String value) {
        if (!pairs.containsKey(value))
            throw new IllegalArgumentException(value);
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
            case StreamingConstants.SAMPLER_WINDOW:
                sampler = new WindowSampler();
                break;
            case StreamingConstants.SAMPLER_FLOATING_WINDOW:
                sampler = new FloatingWindowSampler(getInt(pairs,StreamingConstants.SAMPLER_PAR_SIZE));
                break;
            case StreamingConstants.SAMPLER_RESERVOIR:
                addSamplerReservoir(pairs);
                break;
            default:
                throw new IllegalArgumentException("Unknown filter command: " + mode);
        }
    }

    private void addSamplerReservoir(Map<String, String> pairs) {
        if (pairs.containsKey(StreamingConstants.MODE)){
            int size = getInt(pairs,StreamingConstants.SAMPLER_PAR_SIZE);
            if (pairs.get(StreamingConstants.MODE).equals(StreamingConstants.SAMPLER_MODE_FIX)) {
                int offset = getInt(pairs,StreamingConstants.SAMPLER_PAR_OFFSET);
                sampler = new FixedReservoirSampler(size, offset);
            }
            if (pairs.get(StreamingConstants.MODE).equals(StreamingConstants.SAMPLER_MODE_DYNAMIC)) {
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
            case StreamingConstants.FILTER_MODE_COOCURENCE:
                filters.add(new CoocurentFilter(getInt(pairs,StreamingConstants.FILTER_PAR_EXPIRATION_TIME)));
                break;
            case StreamingConstants.FILTER_MODE_FLAG:
                filters.add(new FlagFilter());
                break;
            case StreamingConstants.FILTER_MODE_USER_CACHE:
                int exponent = getInt(pairs,StreamingConstants.FILTER_PAR_EXPONENT);
                int expirationTime = getInt(pairs,StreamingConstants.FILTER_PAR_EXPIRATION_TIME);
                int size = getInt(pairs,StreamingConstants.FILTER_PAR_SIZE);
                filters.add(new CacheFilter(exponent,expirationTime,size));
                break;
            default:
                throw new IllegalArgumentException("Unknown filter command: " + mode);
        }
    }

    public void addHeuritic(String mode)
    {
        switch (getMode(mode)){
            case StreamingConstants.HEURISTIC_MODE_RANDOM:
                heuristic = new RandomHeuristic();
                break;
            case StreamingConstants.HEURISTIC_MODE_ITERATOR:
                heuristic = new IteratorHeuristic();
                break;
            case StreamingConstants.HEURISTIC_MODE_POPULAR:
                heuristic = new PopularHeuristic();
                break;
            case StreamingConstants.HEURISTIC_MODE_RECENT:
                heuristic = new RecentHeuristic();
                break;
            default:
                throw new IllegalArgumentException("Unknown filter command: " + mode);
        }
    }

    public StreamingManager construct()
    {
        if (sampler == null
                || heuristic == null
                || dataFrame == null)
            throw new IllegalArgumentException("Sampler, heuristic and data frame has to be defined!");
        StreamingExecutor executor = new StreamingExecutor(sampler,heuristic);
        dataFrame.assignExecutor(executor);
        return new StreamingManager(dataFrame,filters);
    }

}
