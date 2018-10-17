import java.util.*;
import java.io.RandomAccessFile;
import java.io.IOException;

/** A class for implementing (pseduo) block-reads and writes.
 *  (It's actually just wrappers for the byte-indexing provided by {@link java.io.RandomAccessFile}.)
 */
class BlockIO extends ObjectIan {

    /** Write block# `blockNum` to `f` with `contents`.  Update `s` with resource-use statistics.
     * @pre contents.length == p.BLOCK_SIZE
     * This can cause `f`s length to become larger.
     */
    static /*@NonNull*/ void writeBlock( RandomAccessFile f, long blockNum, byte[] contents, /*@NonNull*/ ParameterSet p, /*@NonNull*/ Stats s ) 
    throws IOException {
        s.setTimer("writeBlock");
        f.seek(blockNum * p.BLOCK_SIZE);
        f.write( contents );
        s.timeSpentWriting += s.timeSince("writeBlock");
        ++(s.numBlockWrites);
        }
    /** (a version using Stats.DEFAULT) */
    static /*@NonNull*/ void writeBlock( /*@NonNull*/ RandomAccessFile f, long blockNum, byte[] contents, /*@NonNull*/ ParameterSet p ) throws IOException { writeBlock(f,blockNum,contents,p,Stats.DEFAULT); }
    /** (a version using ParameterSet.DEFAULT and Stats.DEFAULT) */
    static /*@NonNull*/ void writeBlock( /*@NonNull*/ RandomAccessFile f, long blockNum, byte[] contents ) throws IOException { writeBlock(f,blockNum,contents,ParameterSet.DEFAULT); }

    /** @return block# `blockNum` from `f`.  Update `s` with resource-use statistics.
     * @post the returned array.size == p.BLOCK_SIZE
     */
    static /*@NonNull*/ byte[] readBlock( /*@NonNull*/ RandomAccessFile f, long blockNum, /*@NonNull*/ ParameterSet p, /*@NonNull*/ Stats s ) throws IOException {
        s.setTimer("readBlock");
        f.seek(blockNum * p.BLOCK_SIZE);
        byte[] contents = new byte[p.BLOCK_SIZE];
        int bytesRead = f.read( contents );
        if (bytesRead == -1) {
            throw new IOException(String.format("Reading past end of file (block #%d doesn't exist?)", blockNum));
            }
        else if (bytesRead != p.BLOCK_SIZE && !p.FILE_MAY_END_IN_PARTIAL_BLOCK) { 
            throw new IOException(String.format("Only read %d bytes (due to end-of-file in middle of block %d?)", bytesRead, blockNum));
            }
        // else we read the entire block successfully.
        s.timeSpentReading += s.timeSince("readBlock");
        ++s.numBlockReads;
        return contents;
        }
    /** (a version using Stats.DEFAULT) */
    static /*@NonNull*/ byte[] readBlock( /*@NonNull*/ RandomAccessFile f, long blockNum, /*@NonNull*/ ParameterSet p ) throws IOException { return readBlock(f,blockNum,p,Stats.DEFAULT); }
    /** (a version using ParameterSet.DEFAULT and Stats.DEFAULT) */
    static /*@NonNull*/ byte[] readBlock( /*@NonNull*/ RandomAccessFile f, long blockNum ) throws IOException { return readBlock(f,blockNum,ParameterSet.DEFAULT); }

    
    
    /** @return a string representation of `content` (the indices in [start,stop)),
     * indented and spaced according to p.LINE_LENGTH.
     */
    static /*@NonNull*/ String toString( /*@NonNull*/ byte[] content, int start, int stop, /*@NonNull*/ ParameterSet p ) {
        StringBuffer sb = new StringBuffer();
        int linePos = 0;
        String padFront = stringTimes( start % p.RECORD_SIZE, PAD_STR );
        String padBack  = stringTimes( (p.RECORD_SIZE - (stop % p.RECORD_SIZE)) %p.RECORD_SIZE, PAD_STR );

        sb.append( padFront );
        linePos += padFront.length();

        for ( int i=start;  i<stop; /* skip */ ) { // write byte#i
            sb.append( charConverter.getOrDefault((char) content[i], (char)content[i]) );
            ++i;
            ++linePos;
            if (i % p.RECORD_SIZE == 0) {
                sb.append(RECORD_SEPARATOR);
                linePos += RECORD_SEPARATOR.length();
                if (linePos >= p.LINE_LENGTH) {
                    sb.append('\n');
                    linePos = 0;
                    }
                }
            }
        sb.append( padBack );
        linePos += padBack.length();
        
        if (sb.charAt(sb.length()-1) != '\n') sb.append('\n');
        return sb.toString();
        }
    /** As @link{#toString(byte[],int,int,ParameterSet)}, using @link{ParameterSet#DEFAULT}. */
    static /*@NonNull*/ String toString( /*@NonNull*/ byte[] content, int start, int stop ) { return BlockIO.toString( content, start, stop, ParameterSet.DEFAULT ); }
    /** As @link{#toString(byte[],int,int,ParameterSet)}, with `start`=0 and `stop`=`content.length`. */
    static /*@NonNull*/ String toString( /*@NonNull*/ byte[] content, ParameterSet p ) {return BlockIO.toString( content, 0, content.length, p ); }
    /** A version which displays the initial `stop` bytes of `content`. */
    /** As @link{#toString(byte[],int,int,ParameterSet)}, using `start`=0. */
    static /*@NonNull*/ String toString( /*@NonNull*/ byte[] content, int stop ) { return BlockIO.toString( content, 0, stop ); }
    /** As @link{#toString(byte[],int,int,ParameterSet)}, with `start`=0 and `stop`=`content.length` and @link{ParameterSet#length}. */
    static /*@NonNull*/ String toString( /*@NonNull*/ byte[] content ) {return BlockIO.toString( content, content.length ); }
    /** A version which displays the entire `content`. */

    private static final String PAD_STR = "␣";
    private static final String RECORD_SEPARATOR = " ";

    
    private static Map<Character,Character> charConverter = new HashMap<>();
    static {
        charConverter.put('\n','↵');
        charConverter.put('\r','↵');
        charConverter.put('\t','⇥');
        charConverter.put('\0','⓪');
        }
    
    
    /** @return the number of blocks in the file `f`.
     * (Roughly, just `f.length()/p.BLOCK_SIZE`, but with some sanity-checking.  A convenience function.)
     */
    static long numBlocks( RandomAccessFile f, ParameterSet p ) throws IOException {
        long len = f.length();
        long blocks = len / p.BLOCK_SIZE;
        boolean perfectFit = (blocks*p.BLOCK_SIZE == len);
        if (!perfectFit && !p.FILE_MAY_END_IN_PARTIAL_BLOCK) 
            throw new IOException( String.format( "File shouldn't have a fractional number of blocks: "
                                                + "len %d, blocks %d, block size %d; excess %d.", 
                                                  len, blocks, p.BLOCK_SIZE, len % p.BLOCK_SIZE ) );
        return blocks + (perfectFit ? 0 : 1);
        }
    /** (A version using ParameterSet.DEFAULT.) */
    static long numBlocks( RandomAccessFile f ) throws IOException { return numBlocks(f, ParameterSet.DEFAULT); }
        
    
    /** @return `s` duplicated `n` times. */
    static /*@NonNull*/ String stringTimes( int n, /*@NonNull*/ String s ) {
        StringBuffer resultSoFar = new StringBuffer();
        for (int i=1;  i<=n;  ++i) resultSoFar.append(s);
        return resultSoFar.toString();
        }

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
