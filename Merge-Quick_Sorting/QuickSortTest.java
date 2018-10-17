import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;
import java.util.Arrays;

/** Unit tests. */
public class QuickSortTest {


    /** Is data[0:boundary] all &le; than pivot, and data[boundary:] all &ge; pivot?   View as records of length `pivot.length`. */
    boolean isValidPartition( byte[] data, byte[] pivot, int boundary ) {
        int k = pivot.length;
        assertEquals( 0, data.length % k ); // sanity check only
        boolean validSoFar = true;
        validSoFar = validSoFar && (1 <= boundary && boundary < data.length/k); // both partitions non-empty
        for (int i=0;         i<boundary;       ++i) validSoFar = validSoFar && (Arrays.compare(data,i*k,(i+1)*k, pivot,0,k) <= 0);
        for (int i=boundary;  i<data.length/k;  ++i) validSoFar = validSoFar && (Arrays.compare(data,i*k,(i+1)*k, pivot,0,k) >= 0);
        return validSoFar; 
        }

    /** Is data sorted (non-descending), viewed as records of length y? */
    boolean isSorted( byte[] data, int k ) {
        boolean validSoFar = true;
        for (int i=1;  i<(data.length/k);  ++i) validSoFar = validSoFar && (Arrays.compare(data,(i-1)*k,i*k, data,i*k,(i+1)*k) <= 0);
        return validSoFar; 
        }

    @Test
    public void testPartition() {
        // test every element of `testDatas`, with each possible partition, for y=1,2,3,4 (if that record-size divides the array evenly). 
        for ( byte[] data : testDatas) {
            for (int k : List.of(1,2,3,4)) {
                if (data.length % k == 0 && data.length/k >= 2) { // can't get two non-empty partitions, w/o at least 2 records.
                    for (int pivotIndex = 0;  pivotIndex < data.length/k;  ++pivotIndex) {
                        byte[] pivot = Arrays.copyOfRange(data,pivotIndex*k,(pivotIndex+1)*k);
                        byte[] data2 = Arrays.copyOf(data,data.length);
                        int boundary = QuickSort.partition(data2,pivotIndex,k);
                        dbg("boundary %d for pivot %s in %s", boundary, Arrays.toString(pivot), Arrays.toString(data2) );
                        assertEquals(true,  isValidPartition( data2, pivot, boundary ) );
                        }
                    }
                }
            }

        }
    
    @Test
    public void testQuickSort() {
        // test every element of `testDatas` for y=1,2,3,4 (if that record-size divides the array evenly). 
        for ( byte[] data : testDatas ) {
            for (int k : List.of(1,2,3,4)) {
                if (data.length % k == 0) {
                    byte[] data2 = Arrays.copyOf(data,data.length);  // must *copy* `data`, since sorting mutates, but we want to re-test sorting it with different y.  Boo, mutation!
                    dbg("about to sort (y=%d): %s", k, Arrays.toString(data2) );
                    QuickSort.quicksort(data2, 0/k, data2.length/k, k, new Random(0));
                    dbg("sorted result (y=%d): %s", k, Arrays.toString(data2) );
                    assertEquals(true, isSorted( data2, k ));
                    }
                }
            }
        }

    byte[][] testDatas;

    @org.junit.Before 
    public void setUp() {
         testDatas = new byte[][]{   {}         // size 0
                                    ,{3}        // size 1

                                    ,{3,4}      // size 2, differ
                                    ,{4,3}
                                    ,{3,3}      // size 2, same
                                    
                                    ,{3,4,5}    // size 3
                                    ,{3,5,4}
                                    ,{4,3,5}
                                    ,{4,5,3}
                                    ,{5,3,4}
                                    ,{5,4,3}
                                    
                                    ,{3,3,4}    // size 3, 1 doubled
                                    ,{3,4,3}
                                    ,{4,3,3}
                                    
                                    ,{3,3,3}    // size 3, 1 element tripled
                                    
                                    ,{1,2,3,4}  // size 4, all different
                                    ,{1,2,4,3}
                                    ,{1,3,2,4}
                                    ,{1,3,4,2}
                                    ,{1,4,2,3}
                                    ,{1,4,3,2}
                                    
                                    ,{2,1,3,4}  
                                    ,{2,1,4,3}
                                    ,{2,3,1,4}
                                    ,{2,3,4,1}
                                    ,{2,4,1,3}
                                    ,{2,4,3,1}
                                    
                                    ,{3,1,2,4}
                                    ,{3,1,4,2}
                                    ,{3,2,1,4}
                                    ,{3,2,4,1}
                                    ,{3,4,1,2}
                                    ,{3,4,2,1}
                                    
                                    ,{4,1,2,3}
                                    ,{4,1,3,2}
                                    ,{4,2,1,3}
                                    ,{4,2,3,1}
                                    ,{4,3,1,2}
                                    ,{4,3,2,1}
                                    
                                    ,{1,1,1,1}  // size 4, 1 element quadrupled
                                    
                                    ,{1,1,1,2}  // size 4, 1 element tripled
                                    ,{1,1,2,1}
                                    ,{1,2,1,1}
                                    ,{2,1,1,1}
                                    
                                    ,{1,1,2,3}  // size 4, 1 element doubled
                                    ,{1,1,3,2}  //  ..doubled element leads
                                    ,{1,2,1,3}
                                    ,{1,2,3,1}
                                    ,{1,3,1,2}
                                    ,{1,3,2,1}
                                    
                                    ,{2,1,1,3}   // ..doubled element doesn't lead
                                    ,{2,1,3,1}
                                    ,{2,3,1,1}
                                    
                                    ,{3,1,1,2}
                                    ,{3,1,2,1}
                                    ,{3,2,1,1}
                                    
                                    
                                    ,{1,1,2,2}  // size 4, 2 elements doubled
                                    ,{1,2,1,2}
                                    ,{1,2,2,1}
                                    ,{2,1,1,2}
                                    ,{2,1,2,1}
                                    ,{2,2,1,1}
                                     };
         
        }
    
    
    
    
   


 
    /******************* lowbrow testing framework *****************/
    
    /** Run the tests, not using JUnit. */
    private static void main( String... args ) {
        // CommandLineOptionTest thiss = new Proj01Qs();
        printTstSummary();
        }

    private static int testCount=0;
    private static int failCount=0;
    private static int currColumn=0;
    private static void printTstMsg(String msg) {
        currColumn=0;
        System.err.printf("\n%s\n", msg);
        }
        
    private static void printTstSummary() {
        printTstMsg( String.format("Completed %d tests (%d failed).", testCount, failCount) );
        }
    // A dummy function, to use if I can't get jUnit and its `assertEquals` working.
    private static void xxassertEquals( Object a, Object b ) {
        ++testCount;
        boolean areEqual = (a==null) ? (b==null) : a.equals(b);
        if (areEqual) {
            System.err.printf(".");
            ++currColumn;
            if (currColumn%5==0) System.err.printf(" ");
            }
        else {
            ++failCount;
            System.err.printf("!");
            System.err.printf("test #%d failed; got %s but expected %s.\n", testCount, b.toString() , a.toString() );
            currColumn=0;
            }
        }
    
    
    
    // Define this function locally in each file, so its body can be commented in/out easily.
    static void dbg( String fmt, Object... vals ) {
        //System.err.printf( ">> %s\n", String.format(fmt,vals) );
        }

    }
