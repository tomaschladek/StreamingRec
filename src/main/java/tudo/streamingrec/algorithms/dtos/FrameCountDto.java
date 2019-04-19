package tudo.streamingrec.algorithms.dtos;

import java.util.List;

public class FrameCountDto extends FrameDto {
    public long count;

    public FrameCountDto(int count) {
        super();
        this.count = count;
    }

    public void assignAndClear(List<Long> other, int count) {
        this.collection = other;
        this.count = count;
    }

}
