import java.util.*;
/**  Keep track of a bunch of statistics relevent to `BlockIO`
 *  (total #reads, total time spent writing, etc.).
 *  It also includes `timers` that can be accessed by name.
 */
class Stats {
    /** The number of calls to readBlock made. */
    long numBlockReads;
    /** The number of calls to writeBlock made. */
    long numBlockWrites;
    /** The time spent in `readBlock` (wall-time, in ns). */
    long timeSpentReading;
    /** The time spent in `writeBlock` (wall-time, in ns). */
    long timeSpentWriting;
    /** A bunch of time-stamps (ns since epoch), stored by name.  Lets callers easily set a timestamp, and later retrieve it. */
    Map<String,Long> timers;
    
    /** Set the current time (ns since epoch), associated with `timerName`.
     *  (just a Map of clock-times, made slightly more convenient).
     *  One timer is initialized when `this` is constructed, named `"main"`.
     *  Example of using the timers:
     *      <pre>
     *      Stats s = Stats.default;
     *      s.startTimer( "task 17" );
     *      for (int i=0;  i &lt; 1000; i=i-1-Math.cos(i)) ;
     *      logMsg( "task 17 started %ld ns ago.", timeSince("task 17") );
     *      resetTimer("task 17"); // only if I want to re-use this timer-name.
     *      logMsg( "program has been running for %ld ns.", timeSince("main") );
     *      </pre>
     */
    void setTimer( /*@NonNull*/ String timerName ) { this.timers.put(timerName, System.nanoTime()); }
    /** @return the time since the timer named `timerName` was set (in ns). 
     *  Will throw an exception if the timer was never created.
     */
    long timeSince( /*@NonNull*/ String timerName )  { return System.nanoTime() - this.timers.get(timerName); }
    
    /** Typical constructor: all fields empty, except that `timers.get("main")` contains the time-of-construction. */
    Stats() { 
        this(0,0,0,0,new HashMap<String,Long>());
        this.timers.put("main", System.nanoTime()); 
        }

    /** boilerplate constructor */
    Stats( long _numBlockReads, long _numBlockWrites, long _timeSpentReading, long _timeSpentWriting, Map<String,Long> _timers ) {
        this.numBlockReads = _numBlockReads;
        this.numBlockWrites = _numBlockWrites;
        this.timeSpentReading = _timeSpentReading;
        this.timeSpentWriting = _timeSpentWriting;
        this.timers = _timers;
        }
    

    public String toString() {
        return String.format( "%5d block-reads  (%dms)\n" + "%5d block-writes (%dms)\n",
                              this.numBlockReads, this.timeSpentReading / NS_PER_MS, this.numBlockWrites, this.timeSpentWriting / NS_PER_MS );
        }


    /** A Stats that anybody can look up.  Used as a default parameter.
     *  Common usage: don't bother passing in an explicity Stats when calling methods that want one;
     *  this object will get used instead.  
     *  The default value doesn't need changing, if you only want to collect one set of statistics from the entire run.
     */
    static /*@NonNull*/ Stats DEFAULT = new Stats();

    /** Number of ns in a ms. */
    static final long NS_PER_MS = 1_000_000;
    }
/* #|
@author ibarland
@version 2018-Feb-18

@license: CC-BY 4.0 -- you are free to share and adapt this file
for any purpose, provided you include appropriate attribution.
    https://creativecommons.org/licenses/by/4.0/ 
    https://creativecommons.org/licenses/by/4.0/legalcode 
Including a link to the *original* file satisifies "appropriate attribution".
|# */
