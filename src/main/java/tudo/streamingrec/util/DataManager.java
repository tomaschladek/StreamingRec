package tudo.streamingrec.util;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import tudo.streamingrec.data.Item;
import tudo.streamingrec.data.RawData;
import tudo.streamingrec.data.SplitData;
import tudo.streamingrec.data.Transaction;
import tudo.streamingrec.data.loading.FilteredDataReader;
import tudo.streamingrec.data.session.SessionExtractor;
import tudo.streamingrec.data.splitting.DataSplitter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class DataManager implements IDataManager{

    public SplitData getSplitData(String items, String clicks, boolean isOutputStats, boolean isDeduplicate, boolean isOldFormat, int sessionLengthThreshold, double splitThreshold) throws IOException, ParseException {
        RawData data = new FilteredDataReader().readFilteredData(items,clicks,isOutputStats,isDeduplicate,isOldFormat);

        filterByDate(new Date(116,1,8,0,0,0),data);

        //if a minimum session length is set, filter short sessions
        if (sessionLengthThreshold > 0) {
            removeShortSessions(data,sessionLengthThreshold);
        }

        //in case stats are wanted, print extensive stats
        if (isOutputStats) {
            printStatistics(data);
        }
        return getSplitData(data,splitThreshold);
    }

    private void removeShortSessions(RawData data, int sessionLengthThreshold) {
        System.out.println();
        System.out.println("Filtering sessions shorter than or equal to " + sessionLengthThreshold + " ...");
        // filter data based on too short sessions
        Set<Transaction> transactionsToRemove = new ObjectOpenHashSet<>();
        // create a session storage to filter too short sessions
        SessionExtractor filterExtractor = new SessionExtractor();
        for (Transaction t : data.transactions) {
            filterExtractor.addClick(t);
        }
        //check the length of sessions and remember the transactions that belong to short sessions
        for (List<List<Transaction>> list : filterExtractor.getSessionMap().values()) {
            for (List<Transaction> list2 : list) {
                if (list2.size() <= sessionLengthThreshold) {
                    for (Transaction transaction : list2) {
                        transactionsToRemove.add(transaction);
                    }
                }
            }
        }
        //keep all transactions except the ones that belong to short sessions in a new transaction list
        List<Transaction> filteredTransactions = new ObjectArrayList<>();
        for (Transaction t : data.transactions) {
            if (!transactionsToRemove.contains(t)) {
                filteredTransactions.add(t);
            }
        }
        //print some removal stats
        System.out.println("Removed "
                + (((data.transactions.size() - filteredTransactions.size()) * 100) / data.transactions.size())
                + "%");
        data.transactions = filteredTransactions;
        System.out.println("Number of transactions: " + data.transactions.size());
    }



    private void filterByDate(Date dateTime, RawData data) {
        Long2ObjectOpenHashMap<Item> filteredItems = new Long2ObjectOpenHashMap<>();
        for (Long item : data.items.keySet())
        {
            if(data.items.get(item).updatedAt.before(dateTime))
            {
                filteredItems.put(item,data.items.get(item));
            }
        }
        List<Transaction> filteredTransactions = new ObjectArrayList<>();
        for (Transaction item : data.transactions)
        {
            if(item.timestamp.before(dateTime))
            {
                filteredTransactions.add(item);
            }
        }
        data.items =  filteredItems;
        data.transactions = filteredTransactions;
    }

    private SplitData getSplitData(RawData data, double splitThreshold) {
        // split the data
        DataSplitter splitter = new DataSplitter();
        splitter.setSplitMethodNumberOfEvents(); // split based on the number of
        // events, not the time
        splitter.setSplitThreshold(splitThreshold);
        // split after N% of the events.
        // Everything after that goes into the test set
        return splitter.splitData(data);
    }

    private void printStatistics(RawData data) {
        // overall stats
        Long2IntOpenHashMap clicksPerUser = new Long2IntOpenHashMap();
        Long2IntOpenHashMap clicksPerItem = new Long2IntOpenHashMap();
        // session stats
        SessionExtractor sessionExtractorForStats = new SessionExtractor();
        for (Transaction t : data.transactions) {
            clicksPerItem.addTo(t.item.id, 1);
            clicksPerUser.addTo(t.userId, 1);
            sessionExtractorForStats.addClick(t);
        }

        //clicks per items and user
        DescriptiveStatistics clicksPerUserStats = new DescriptiveStatistics();
        DescriptiveStatistics clicksPerItemStats = new DescriptiveStatistics();
        for (Integer val : clicksPerUser.values()) {
            clicksPerUserStats.addValue(val);
        }
        System.out.println("Clicks per user: " + clicksPerUserStats);
        for (Integer val : clicksPerItem.values()) {
            clicksPerItemStats.addValue(val);
        }
        System.out.println("Clicks per item: " + clicksPerItemStats);

        // some statistics about session length
        DescriptiveStatistics stats = new DescriptiveStatistics();
        DescriptiveStatistics statsPerUser = new DescriptiveStatistics();
        DescriptiveStatistics lengthStats = new DescriptiveStatistics();
        Collection<List<List<Transaction>>> allSessions = sessionExtractorForStats.getSessionMap().values();
        for (List<List<Transaction>> list : allSessions) {
            for (List<Transaction> list2 : list) {
                stats.addValue(list2.size());
                if (list2.size() > 1) {
                    long duration = list2.get(list2.size() - 1).timestamp.getTime()
                            - list2.get(0).timestamp.getTime();
                    lengthStats.addValue(duration);
                }
            }
            statsPerUser.addValue(list.size());

        }
        System.out.println("Clicks per session: " + stats);
        System.out.println("Sessions per user: " + statsPerUser);
        System.out.println("Length of session in MS: " + lengthStats);
    }
}
