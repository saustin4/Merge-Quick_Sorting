import java.util.*;
/** A class to collect the configuration-parameters, for block-based file access.
 */
class ParameterSet extends ObjectIan {
    /** The size of a block, in bytes.  (E.g. used by {@link BlockIO#readBlock(java.io.RandomAccessFile,long)}, etc.) */
    int BLOCK_SIZE;   // must be `int`, because ints are used to index into arrays, and we use `byte[]`
    /** The size of a record, in bytes.  Think of this as the size of a single object (as stored on disk). */
    int RECORD_SIZE;   // how many bytes long one record is.  RECORD_SIZE should divide BLOCK_SIZE evenly.
    /** A random-number generator for miscellaneous use. */
    Random rng;  // for whatever random-numbers may be wanted.

    /** The name of a default log file. (Currently unused.) */
    String logFile;
    /** The length of an l0ad line to use, in display-methods (e.g.&nbsp;{@link BlockIO#toString(byte[])}). */
    int LINE_LENGTH;
    /** Can the last block of a file contain less than one block of real data?  Defaults to false, unless you want to implement extra-credit. */
    boolean FILE_MAY_END_IN_PARTIAL_BLOCK; // false, unless you want to do extra credit (but get it working w/o, first)
    
    
    ParameterSet( int _block_size, int _record_size ) {
        this( _block_size, _record_size, new Random(0) );
        }
    ParameterSet( int _block_size, int _record_size, Random _rng ) {
        this( _block_size, _record_size, _rng, "log.txt", 100, false );
        }
    /** For use in unit testing/debugging, to be able to configure every single field. */
    ParameterSet( Object... args ) { super(args); }
    
    /** A ParameterSet that anybody can look up.  Used as a default parameter.
     *  Common usage: assign to (overwrite) this field exactly once, and then don't bother
     *  passing in an explicity ParameterSet when calling methods that want one.
     */
    static /*@NonNull*/ ParameterSet DEFAULT = new ParameterSet(4096,16);
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
