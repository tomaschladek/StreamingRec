package tudo.streamingrec.algorithms;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;

import java.util.Collections;
import java.util.List;

/**
 * The recommendations of this algorithm are all news articles that were clicked by users,
 * ordered by the time of the last user click.
 * 
 * @author Mozhgan
 *
 */
public class RecentlyClicked extends Algorithm{
	//a linked list of article ids ordered by their last user click time
	public long clickedItem = 0l;

	@Override
	protected void trainInternal(List<Item> items, List<ClickData> clickData) {
		//whenever a click occurs, remove the id of the article that has been clicked
		//from the result list and push it to the front
		if (clickData != null
		&& clickData != Collections.EMPTY_LIST
		&& clickData.size() > 0)
		{
			clickedItem = clickData.get(clickData.size()-1).click.item.id;
		}
	}

	@Override
	public LongArrayList recommendInternal(ClickData clickData) {
		//return an array list copy of the ordered article ids
		LongArrayList list = new LongArrayList();
		list.set(0,clickedItem);
		return list;
	}

}
