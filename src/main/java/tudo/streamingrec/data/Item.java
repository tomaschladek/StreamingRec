package tudo.streamingrec.data;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Meta data about an item
 *
 * @author Mozhgan
 *
 */
public class Item implements Event {
	//the ID of the item
	public long id;
	//the publisher of the news article
	public int publisher;
	//the data when this article was first published
	public Date createdAt;
	public Date updatedAt;
	//the URL of the article
	public String url;
	//the plain-text title of the article
	public String title;
	//the category of the item
	public int category;
	public long version;
	public long flag;
	public long video;
	public String kicker;

	//the plain-text content of the item
	public String text;
	//a set of keywords
	public Object2IntOpenHashMap<String> keywords;

	public Item() {
	}

	/**
	 * Instantiates an item from a line in a csv file
	 *
	 * @param csvLine -
	 * @throws ParseException -
	 */
	public Item(String csvLine) throws ParseException {
		String[] split = csvLine.split(",");
		publisher = Integer.parseInt(split[0]);
		createdAt = parseData(split[1]);
		id = Long.parseLong(split[2]);
		url = split[3];
		title = split[4];
		if (!split[5].equals("null"))
			version = Long.parseLong(split[5]);
		if (!split[6].equals("null"))
			flag = Long.parseLong(split[6]);
		if (!split[7].equals("null"))
			video = Long.parseLong(split[7]);
		if (!split[8].equals("null"))
			kicker = split[8];
		if (!split[9].equals("null"))
			updatedAt = parseData(split[9]);
		if (split.length > 10) {
			category = Integer.parseInt(split[10]);
		}
		if (split.length > 11) {
			text = split[11];
		}
		if (split.length > 12) {
			keywords = new Object2IntOpenHashMap<>();
			String[] keywordArr = split[12].split(Pattern.quote("#"));
			for (String string : keywordArr) {
				String[] split2 = string.split(Pattern.quote("-"));
				keywords.addTo(split2[0], Integer.parseInt(split2[1]));
			}
		}
	}

	private Date parseData(String text) throws ParseException {
		try{
			return Constants.DATE_FORMAT2.parse(text);
		}
		catch (Exception e)
		{
			return Constants.DATE_FORMAT.parse(text);
		}
	}

	/**
	 * Returns the item as a CSV line
	 */
	@Override
	public String toString() {
		String keywords = "";
		//in case there are keywords -> serialize them
		if(this.keywords!=null){
			StringBuilder sb = new StringBuilder();
			ObjectIterator<Entry<String>> fastIterator = this.keywords.object2IntEntrySet().fastIterator();
			while(fastIterator.hasNext()){
				Entry<String> next = fastIterator.next();
				sb.append(next.getKey().replace("-","").replace("#", ""));
				sb.append("-");
				sb.append(next.getIntValue());
				if(fastIterator.hasNext()){
					sb.append("#");
				}
			}
			keywords = sb.toString();
		}

		//write CSV string
		return publisher + Constants.CSV_SEPARATOR + Constants.DATE_FORMAT.format(createdAt) + Constants.CSV_SEPARATOR
				+ id + Constants.CSV_SEPARATOR + url + Constants.CSV_SEPARATOR + title + Constants.CSV_SEPARATOR
				+ version + Constants.CSV_SEPARATOR + flag + Constants.CSV_SEPARATOR + video + Constants.CSV_SEPARATOR + kicker + Constants.CSV_SEPARATOR + Constants.DATE_FORMAT.format(updatedAt) + Constants.CSV_SEPARATOR
				+ category + Constants.CSV_SEPARATOR + text + Constants.CSV_SEPARATOR + keywords;
	}

	/**
	 * Return the publication time of the item as a sorting criterion
	 */
	public Date getEventTime() {
		return updatedAt;
	}

	/**
	 * Items are considered equal if they have the same ID
	 */
	@Override
	public boolean equals(Object item) {
		if (this.id == ((Item) item).id) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * See {@link #equals(Object)}
	 */
	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}
}
