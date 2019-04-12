package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.List;

/**
 * The candidate set of this algorithm is all news in the data set
 * and it selects news articles randomly from this set for recommendation.
 *
 * @author Mozhgan
 *
 */
public class Random extends Algorithm{
	//An unsorted set of all item ids
	private LongOpenHashSet items = new LongOpenHashSet();
	private java.util.Random randomGenerator = new java.util.Random();

	@Override
	protected void trainInternal(List<Item> items, List<ClickData> clickData) {
		//if new items arrive, add their ids to the set of item ids
		for (Item item : items) {
			this.items.add(item.id);
		}
		for (ClickData data : clickData)
		{
			if (!this.items.contains(data.click.item.id))
			{
				this.items.add(data.click.item.id);
			}
		}
	}

	@Override
	public LongArrayList recommendInternal(ClickData clickData) {
		//create a result list and copy the known item ids there
		LongArrayList recs = new LongArrayList(items);

        LongArrayList list = new LongArrayList();
        list.add(recs.getLong(randomGenerator.nextInt(items.size())));
		return list;
	}

}
