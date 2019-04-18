package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.List;

public class Streaming extends Algorithm {

    @Override
    protected void trainInternal(List<Item> items, List<ClickData> transactions) {

    }

    @Override
    protected LongArrayList recommendInternal(ClickData clickData) {
        return null;
    }
}
