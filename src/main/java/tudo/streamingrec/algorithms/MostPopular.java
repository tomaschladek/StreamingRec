package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.List;

/**
 * An algorithm that recommends the most popular articles based on click count
 * regardless of the category.
 * 
 * @author Mozhgan
 *
 */
public class MostPopular extends Algorithm {
	// In this list we keep all articles and their click counts
	protected Long2IntOpenHashMap clickCounter = new Long2IntOpenHashMap();
	private long index = 0;
	private long count = Long.MIN_VALUE;

	@Override
	protected void trainInternal(List<Item> items, List<ClickData> clickData) {
		// iterate through all transactions and increase the click count for the
		// respective item
		for (ClickData c : clickData) {
			clickCounter.addTo(c.click.item.id, 1);
			if (clickCounter.get(c.click.item.id) > count)
			{
				index = c.click.item.id;
				count = clickCounter.get(c.click.item.id);
			}
		}
	}
	
	public LongArrayList recommendInternal(ClickData clickData) {
		//return the items sorted by their click count
		LongArrayList list = new LongArrayList();
		list.add(index);
		return list;
	}
}
