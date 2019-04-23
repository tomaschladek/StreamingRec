package testing.streamingrec.algorithms.helper;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tudo.streamingrec.algorithms.helper.UserCache;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserCacheTest {

    private UserCache cache;
    private Date timestamp;

    @BeforeEach
    void setup() {
        this.cache = new UserCache(2,5,1);
        this.timestamp = new Date(2,0,0,0,0,0);
    }

    @Test
    void tryUpsert() {
        assertTrue(cache.tryUpsert(1,1));
        assertTrue(cache.tryUpsert(1,2));
        assertTrue(cache.tryUpsert(1,1));
        assertFalse(cache.tryUpsert(1,1));
    }

    @Test
    void tryUpsertAnonym() {
        assertTrue(cache.tryUpsert(0,1));
        assertTrue(cache.tryUpsert(0,1));
    }

    @ParameterizedTest
    @CsvSource({ "1,false", "2,false","3,false","4,false","5,false","6,true" })
    void update(int index, boolean expectedValue) {
        assertTrue(cache.tryUpsert(1,1));
        cache.update(timestamp);
        assertTrue(cache.tryUpsert(1,1));
        timestamp = DateUtils.addSeconds(timestamp,index);
        cache.update(timestamp);
        assertEquals(expectedValue,cache.tryUpsert(1,1));
    }

    @Test
    void getHistory() {
        assertEquals(0,cache.getHistory(1).size());
        cache.tryUpsert(1,1);
        assertEquals(1,cache.getHistory(1).size());
    }

    @Test
    void getLongerHistory() {
        this.cache = new UserCache(2,5,2);
        cache.tryUpsert(1,1);
        cache.tryUpsert(1,2);
        cache.tryUpsert(1,3);
        assertEquals(2,cache.getHistory(1).size());
        assertEquals(2,cache.getHistory(1).get(0));
        assertEquals(3,cache.getHistory(1).get(1));
    }
}