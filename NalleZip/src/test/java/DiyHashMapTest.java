

import com.nallezip.app.util.DiyHashMap;
import com.nallezip.app.util.DiyContent;
import com.nallezip.app.util.DiySet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tallbera
 */
public class DiyHashMapTest {

    public DiyHashMapTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testPut() {
        DiyHashMap<Integer, Integer> diy = new DiyHashMap();
        assertEquals(diy.size(), 0);
        int test = 1;
        int test2 = 2;
        diy.put(test, test2);
        assertEquals(diy.size(), 1);
        int answer = diy.get(test);
        assertEquals(test2, answer);
        int test3 = 3;
        diy.put(test, test3);
        int answer2= diy.get(test);
    }

    @Test
    public void testGet() {
        DiyHashMap<Integer, Integer> diy = new DiyHashMap();
        int test = 1;
        int test2 = 2;
        diy.put(test, test2);
        int answer = diy.get(test);
        assertEquals(test2, answer);
        Integer answer2 = diy.get(test2);
        boolean check = false;
        if(answer2==null){
            check = true;
        }
        assertTrue(check);
        
    }

    @Test
    public void testSize() {
        DiyHashMap<Integer, Integer> diy = new DiyHashMap();
        assertEquals(0, diy.size());
        diy.put(1, 1);
        assertEquals(1,diy.size());
    }

    @Test
    public void testContainsKey() {
        DiyHashMap<String, String> diy = new DiyHashMap();
        String key = "key";
        String value = "value";
        diy.put(key, value);
        boolean answer = diy.containsKey(key);
        assertTrue(answer);
        answer = diy.containsKey(value);
        assertFalse(answer);
    }

    @Test
    public void testKeySetForCharacters() {
        DiyHashMap<Character, Integer> diy = new DiyHashMap();
        char c = 'c';
        char d = 'd';
        diy.put(c, 1);
        diy.put(d, 2);

        DiySet set = diy.keySetForCharacters();
        assertEquals(set.length(),2);
        assertEquals("{c, d, }",set.toString());
    }
    
}
