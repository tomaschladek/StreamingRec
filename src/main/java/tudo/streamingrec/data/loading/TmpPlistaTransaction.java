package tudo.streamingrec.data.loading;

import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import tudo.streamingrec.data.Constants;
import tudo.streamingrec.data.Event;
import tudo.streamingrec.data.Item;
import tudo.streamingrec.data.Transaction;

/**
 * A temporary transaction class only to be used while converting plista
 * logs to their final form. Contains more information than necessary 
 * (e.g. category, which is also contained in the Item class). After merging 
 * via {@link JoinPlistaTransactionsWithMetaInfo} this objects of this class
 * are converted to regular transactions.
 *
 * @author Mozhgan
 *
 */
public class TmpPlistaTransaction implements Event {
	public int publisher;
	public Item item;
	public int category;
	public long cookie;
	public Date timestamp;
	public long limit;
	public long deviceType;
	public long geoUser;
	public long geoUserZip;
	public long browser;
	public long language;
	public long os;
	public long gender;
	public long age;
	public long income;
	public Object2IntOpenHashMap<String> keywords;

	public TmpPlistaTransaction() {
	}

	/**
	 * Instantiates an transaction from a line in a csv file
	 *
	 * @param csvLine -
	 * @param items -
	 */
	public TmpPlistaTransaction(String csvLine, Map<Long, Item> items) {
		String[] splitT = csvLine.split(",");
		publisher = Integer.parseInt(splitT[0]);
		if (!splitT[2].equals("null")) {
			long itemId = Long.parseLong(splitT[2]);
			if (items.containsKey(itemId)) {
				item = items.get(itemId);
			} else if (itemId != 0){
				item = new Item();
				item.id = itemId;
			} else{

			}
		}
		if (!splitT[1].equals("null")) {
			category = Integer.parseInt(splitT[1]);
		}
		if (item != null) {
			if (item.category == 0) {
				item.category = this.category;
			} else {
				this.category = item.category;
			}
		}
		cookie = Long.parseLong(splitT[3]);
		timestamp = new Date(Long.parseLong(splitT[4]));
		if (!splitT[5].equals("null"))
			limit = Long.parseLong(splitT[5]);
		if (!splitT[6].equals("null"))
			income = Long.parseLong(splitT[6]);
		if (!splitT[7].equals("null"))
			geoUser = Long.parseLong(splitT[7]);
		if (!splitT[8].equals("null"))
			geoUserZip = Long.parseLong(splitT[8]);
		if (!splitT[9].equals("null"))
			deviceType = Long.parseLong(splitT[9]);
		if (!splitT[10].equals("null"))
			browser = Long.parseLong(splitT[10]);
		if (!splitT[11].equals("null"))
			age = Long.parseLong(splitT[11]);
		if (!splitT[12].equals("null"))
			os = Long.parseLong(splitT[12]);
		if (!splitT[13].equals("null"))
			gender = Long.parseLong(splitT[13]);
		if (!splitT[14].equals("null"))
			language = Long.parseLong(splitT[14]);
		if (splitT.length > 15) {
			keywords = new Object2IntOpenHashMap<>();
			String[] keywordArr = splitT[15].split(Pattern.quote("#"));
			for (String string : keywordArr) {
				String[] split2 = string.split(Pattern.quote("-"));
				keywords.addTo(split2[0], Integer.parseInt(split2[1]));
			}
		}
	}

	@Override
	public String toString() {
		String itemID;
		if (item == null) {
			itemID = "null";
		} else {
			itemID = String.valueOf(item.id);
		}
		String keywords = "";
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
		return publisher + Constants.CSV_SEPARATOR + category + Constants.CSV_SEPARATOR + itemID
				+ Constants.CSV_SEPARATOR + cookie + Constants.CSV_SEPARATOR + timestamp.getTime()
				+ Constants.CSV_SEPARATOR + limit + Constants.CSV_SEPARATOR + income
				+ Constants.CSV_SEPARATOR + geoUser + Constants.CSV_SEPARATOR + geoUserZip
				+ Constants.CSV_SEPARATOR + deviceType + Constants.CSV_SEPARATOR + browser
				+ Constants.CSV_SEPARATOR + age + Constants.CSV_SEPARATOR + os
				+ Constants.CSV_SEPARATOR + gender + Constants.CSV_SEPARATOR + language
				+ Constants.CSV_SEPARATOR + keywords;
	}

	public Date getEventTime() {
		return timestamp;
	}

	/**
	 * Converts this temporary plista transaction object into normal Transaction object 
	 * @return a transaction object with the same attributes as in this instance
	 */
	public Transaction toTransaction(){
		Transaction t = new Transaction();
		t.item = item;
		t.timestamp = timestamp;
		t.userId = cookie;
		return t;
	}
}
