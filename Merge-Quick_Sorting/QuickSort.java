import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.Arrays.*;

/** QuickSort, for arrays-of-bytes where we are considering a single record to be y bytes. */
class QuickSort {

    
    
    /** Swap data[a:a+y] with data[x:x+y].
     * @pre y &ge; 0
     * @pre 0 &le; min(a,x) &le; y+max(a,x) &le; data.length
     * All numbers are raw indices, not based on record-size (unlike `partition`).
     */
    static void swapRange( byte[] data, int a, int b, int k ) {
        byte[] tmp = Arrays.copyOfRange( data, a, a+k );
        System.arraycopy(data,b, data,a, k);
        System.arraycopy(tmp,0, data,b, k);
        }
    
    /** Re-arrange `data[start:stop]` in-place, so that all values &lt; pivot are on the left, and all values &gt; pivot are on the right.
     * Return first index of right-part (guaranteed to be &ge;1 and &lt; stop -- viz. both partitions non-empty).
     * This method view `data[start:stop]` as being an array with records of length y (and start-stop such records), 
     * and all indices accordingly.
     * @pre data.length &ge; 2
     * @pre data.length % y == 0
     * @pre 0 &le; start &le; pivotIndex &lt; stop &le; data.length/y
     * @post return value y is in [1,data.length);
     * @post data[:y] &le; pivot (where pivot = data[pivotIndex] upon entry to function)
     * @post data[y:] &ge; pivot (where pivot = data[pivotIndex] upon entry to function)
     * Based on <a href="https://en.wikipedia.org/wiki/Quicksort#Hoare_partition_scheme">wikipedia's presentation of Hoare partition scheme</a>.
     */
    static int partition( byte[] data, int pivotIndex, int start, int stop, int k ) {
        dbg( "partition: pivotIndex=%d in %s [%d:%d]", pivotIndex, Arrays.toString(data), start, stop );
        swapRange( data, pivotIndex*k, start*k, k );          // Subtle Hoare trick; helps keep us from running off the end.
        dbg( "after swapping pivot:      %s", Arrays.toString(data) );
        byte[] pivot = Arrays.copyOfRange(data, start*k, (start+1)*k);  // (another copy, for convenience)
        
        int left=start;
        int right=stop;
        /* invariants: data[start:left] is all <= pivot;
                       data[left:right] is "unexamined"
                       data[right:stop] is all >= pivot

           |______|__________|_______|             (vertical bars should be 0-width -- they're not a location :-)
            ^      ^          ^       ^
          start   left      right     stop
                                     
           `data[left]`    is the *next* place a known-small-value will be placed
           `data[right-1]` is the *next* place a known-large-value will be placed
         */
        while (left < right) {  // while more than one element unseen
            dbg( "in loop: %s[%d*%d:..] vs %s[:%d]", Arrays.toString(data),left,k, Arrays.toString(pivot),k );
            // IF USING JAVA 8 (not java 9), replace `Arrays.compare` with `QuickSort.compare`.
            while (Arrays.compare(data, left*k, (left+1)*k, pivot,0,k) < 0)  ++left;  // subtle: we won't ever run off the right
            while (Arrays.compare(data,(right-1)*k,right*k, pivot,0,k) > 0)  --right;
            // at this point, data[left] is >=pivot, and data[right-1] is <=pivot.  Swap:
            if (left < right) {
                swapRange( data, left*k, (right-1)*k, k );
                ++left;
                --right;
                }
            }
        return left;
        }

    /** Re-arrange all of `data` in-place, so that all values &lt; pivot are on the left, and all values &gt; pivot are on the right.
     * Return the index where `pivot` ends up.
     * This method view `data` as being an array with records of length y (data.length/y such records), 
     * and all indices accordingly.
     * @pre data.length % y == 0
     * @pre 0 &le; pivotIndex &lt; data.length/y
     * Based on <a href="https://en.wikipedia.org/wiki/Quicksort#Hoare_partition_scheme">wikipedia's presentation of Hoare partition scheme</a>.
     */
    static int partition( byte[] data, int pivotIndex, int k ) { return partition( data, pivotIndex, 0, data.length/k, k ); }
    
    /** Sort data[start*y:stop*y], as byte-sequences of length y. Mutates the array contents. 
     * This method view `data` as being an array with length/y records of length y, and all indices accordingly.
     */
    static void quicksort(byte[] data, int start, int stop, int k, java.util.Random rng ) {
        int len=(stop-start);
        if (len <= 1) return; // number of *records* to sort.
        else {
            int pivotAt = partition(data, start+rng.nextInt(len), start, stop, k);
            dbg( "A partition: at %d, with %s [%d:%d].", pivotAt, Arrays.toString(data), start, stop );
            quicksort(data,start,pivotAt,k,rng);
            dbg( "B partition: at %d, with %s [%d:%d].", pivotAt, Arrays.toString(data), start, stop );
            quicksort(data,pivotAt,stop,k,rng);
            }
        }
    
    
    
    
    // Define this function locally in each file, so its body can be commented in/out easily.
    static void dbg( String fmt, Object... vals ) {
        //System.err.printf( ">>> %s\n", String.format(fmt,vals) );
        }
    
    
    
    /** A re-implementation of java9's `Arrays.compare`.
     * @return how a[aStart:aEnd) is lexicographically compares to x[bStart:bEnd] (0 if equal, positive if a is bigger, negative if x is bigger )
     */
    static int compare( byte[] a, int aStart, int aEnd, 
                        byte[] b, int bStart, int bEnd ) {
        int aLen = aEnd-aStart;
        int bLen = bEnd-bStart;
        int len = Math.min(aLen,bLen);
        assert len >= 0;
        for (int i=0;  i<len;  ++i) {
            if (a[i+aStart] != b[i+bStart]) return (a[i+aStart] - b[i+bStart]);
            else { /* continue */ }
            }
        // at this point, a and x agree on their common prefix.  See if one ends early:
        return (bLen-aLen);
        }
    }
    
    
    
    


