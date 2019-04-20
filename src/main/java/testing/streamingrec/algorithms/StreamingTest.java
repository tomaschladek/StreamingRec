package testing.streamingrec.algorithms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tudo.streamingrec.algorithms.Streaming;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Item;
import tudo.streamingrec.data.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class StreamingTest {

    private Streaming algorithm;

    @BeforeEach
    public void setUp(){
        this.algorithm = new Streaming();
    }

    @Test
    public void smokeTest(){
        initialize(1);
    }

    @ParameterizedTest
    @CsvSource({"1,3,0,1",
            "0,3,1,1",
            "1,3,2,1",
            "2,2,0,1",
            "0,2,2,1",
            "1,1,0,1",
            "0,1,1,1",
            "1,1,2,1",
            "0,1,0,2",
            "0,1,1,2",
            "0,1,2,2",
            "0,1,0,3",
            "1,1,1,3",
            "2,1,2,3"})
    public void executeAlgorithm(long expectedValue, long userId, int itemId,int cacheSize){
        initialize(cacheSize);
        List<Item> items = new ArrayList<>();
        items.add(createItem(0,0));
        items.add(createItem(1,0));
        items.add(createItem(2,0));
        List<ClickData> transactions = new ArrayList<>();
        transactions.add(createClick(1,items.get(0),0));
        transactions.add(createClick(1,items.get(1),1));
        transactions.add(createClick(1,items.get(2),2));
        transactions.add(createClick(2,items.get(1),3));
        algorithm.train(items,transactions);
        Assertions.assertEquals(expectedValue,algorithm.recommend(createClick(userId,items.get(itemId),4)).getLong(0));
    }

    private ClickData createClick(long userId, Item item, long ms) {
        ClickData click = new ClickData();
        click.click = new Transaction();
        click.click.userId = userId;
        click.click.item = item;
        click.click.timestamp = new Date(ms);

        return click;
    }

    protected Item createItem(long itemId, int flag) {
        Item item = new Item();
        item.id = itemId;
        item.flag = flag;
        item.updatedAt = new Date(0);
        return item;
    }

    private void initialize(int cacheSize) {
        algorithm.setCache("userCache|exponent:10;size:"+cacheSize + ";expirationTime:5");
        algorithm.setHeuristic("popular|");
        algorithm.setAreAnonymousAllowed(true);
        algorithm.setDataFrame("overlap|frames:100;mode:count;size:50");
        algorithm.setSampler("fixed|frames:100;mode:count;size:50");
        algorithm.setIsItem(false);
        algorithm.setIsTransaction(true);
        algorithm.initialize();
    }


    @ParameterizedTest
    @CsvSource({"1,1000,2",
            "0,1000,1",
            "1,1000,0",
            "1,111,0",
            "1,111,1",
            "1,111,2"
    })
    public void recommendAfterWindowSwitch(long expectedValue, long userId, int itemId){
        initialize(1);
        List<Item> items = new ArrayList<>();
        items.add(createItem(0,0));
        items.add(createItem(1,0));
        items.add(createItem(2,0));
        List<ClickData> transactions = new ArrayList<>();
        transactions.add(createClick(111,items.get(0),0));
        for (int index = 1; index < 101; index++)
        {
            int itemIndex = index <60 ? 0 : 1;
            transactions.add(createClick(1,items.get(itemIndex),0));
        }

        algorithm.train(items,transactions);
        Assertions.assertEquals(expectedValue,algorithm.recommend(createClick(userId,items.get(itemId),4)).getLong(0));
    }
}