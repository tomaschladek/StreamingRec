package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.List;

/**
 * An algorithm that recommends the articles in the order of their publication/updating (newest to oldest)
 * 
 * @author Mozhgan
 *
 */
public class MostRecent extends Algorithm {
	//an ordered list of the most recently published/updated news articles
	private LongArrayList mostRecentItems = new LongArrayList();

	public void trainInternal(List<Item> items, List<ClickData> clickData) {
		//if new articles arrive. check if they are already known
		//if so remove the old entry from the list
		//in any case, add the new article id to the list
		for (Item item : items) {
			if (mostRecentItems.contains(item.id)) {
				mostRecentItems.rem(item.id);
			}
			mostRecentItems.add(0, item.id);
		}
		for(ClickData data : clickData) {
			if (mostRecentItems.contains(data.click.item.id)) {
				mostRecentItems.rem(data.click.item.id);
			}
			mostRecentItems.add(0, data.click.item.id);
		}
	}

	public LongArrayList recommendInternal(ClickData clickData) {
		//just return the already ordered list of articles
		LongArrayList list = new LongArrayList();
		list.add(mostRecentItems.getLong(0));
		return list;
	}

}
