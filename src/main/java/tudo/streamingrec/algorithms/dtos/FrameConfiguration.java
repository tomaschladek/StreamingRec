package tudo.streamingrec.algorithms.dtos;

public class FrameConfiguration {
    public int timeFrameIndex;
    public int[] timeFrame;

    public FrameConfiguration(int[] timeFrame) {
        this.timeFrameIndex = 0;
        this.timeFrame = timeFrame;
    }

    public int getNext() {
        int value =  timeFrame[timeFrameIndex];
        timeFrameIndex = (timeFrameIndex + 1) % timeFrame.length;
        return value;
    }
}
