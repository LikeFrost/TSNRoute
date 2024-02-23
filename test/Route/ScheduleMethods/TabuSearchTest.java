package Route.ScheduleMethods;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TabuSearchTest {
    @Test
    void testPermute(){
        TabuSearch tabuSearch = new TabuSearch();
        List<Integer> nums = Arrays.asList(1,2,3,4,5);
        System.out.println(tabuSearch.permute(nums,10));
    }

}