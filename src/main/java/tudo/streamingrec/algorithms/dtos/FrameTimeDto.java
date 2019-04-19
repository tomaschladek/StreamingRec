package tudo.streamingrec.algorithms.dtos;

import java.util.ArrayList;
import java.util.Date;

public class FrameTimeDto extends FrameDto {
    public Date timestampThreshold;

    public FrameTimeDto() {
        super();
        this.timestampThreshold = new Date(0,0,0,0,0,0);
    }

    public void assignAndClear(FrameTimeDto other) {
        this.collection = other.collection;
        other.collection = new ArrayList<>();
    }
}
