package tudo.streamingrec.util;

import tudo.streamingrec.data.SplitData;

import java.io.IOException;
import java.text.ParseException;

public interface IDataManager{
    SplitData getSplitData(String items, String clicks, boolean isOutputStats, boolean isDeduplicate, boolean isOldFormat, int sessionLengthThreshold, double splitThreshold) throws IOException, ParseException;
}
