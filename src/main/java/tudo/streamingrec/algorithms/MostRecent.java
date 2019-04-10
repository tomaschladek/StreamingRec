package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.Collections;
import java.util.List;

/**
 * An algorithm that recommends the articles in the order of their publication/updating (newest to oldest)
 * 
 * @author Mozhgan
 *
 */
public class MostRecent extends Algorithm {
	//an ordered list of the most recently published/updated news articles
	private long mostRecentItem = 0l;

	public void trainInternal(List<Item> items, List<ClickData> clickData) {
		//if new articles arrive. check if they are already known
		//if so remove the old entry from the list
		//in any case, add the new article id to the list
		if (items != null
				&& items != Collections.EMPTY_LIST
				&& items.size()>0)
		{
			mostRecentItem = items.get(items.size()-1).id;
		}
	}

	public LongArrayList recommendInternal(ClickData clickData) {
		//just return the already ordered list of articles
		LongArrayList list = new LongArrayList();
		list.add(mostRecentItem);
		return list;
	}

}
