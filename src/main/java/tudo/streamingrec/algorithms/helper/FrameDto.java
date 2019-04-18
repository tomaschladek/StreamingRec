package tudo.streamingrec.algorithms.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FrameDto {
    private final int capacity;
    public List<Long> collection;
    public Date timestampThreshold;

    public FrameDto(int capacity) {
        this.capacity = capacity;
        this.collection = new ArrayList<>(capacity);
        this.timestampThreshold = new Date(0,0,0,0,0,0);
    }

    public void assignAndClear(FrameDto other) {
        this.collection = other.collection;
        other.collection = new ArrayList<>(capacity);
    }
}
