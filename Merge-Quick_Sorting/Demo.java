import java.util.*;
import java.io.*;

/** Some example functions demonstrating how to use the block-level IO functions of {@link BlockIO}.
 */
class Demo {
    
    /** A sample function which formats &amp; prints out the contents of `fname` to STDOUT,
     * taking `p.BLOCK_SIZE`, `p.RECORD_SIZE`, and `p.LINE_LENGTH` into account.
     */
    static void displayFile( String fname, ParameterSet p ) throws IOException {
        System.out.printf("Contents of %s\n", fname);
        RandomAccessFile f = new RandomAccessFile(fname,"r");
        long n = BlockIO.numBlocks(f,p);
        for (int i=0;  i<n;  ++i) {
            System.out.print( BlockIO.toString( BlockIO.readBlock(f,i,p), p ) );
            }
        f.close();
        }
    static void displayFile( String fname ) throws IOException { displayFile(fname,ParameterSet.DEFAULT); }

    /** A sample function which copies `src` to `target` block-by-block (using `p.BLOCK_SIZE`).
     * It overwrites any existing file named `target`.
     */
    static void copyFile( String src, String target, ParameterSet p ) throws IOException {
        RandomAccessFile in = new RandomAccessFile(src,"r");
        RandomAccessFile out = new RandomAccessFile(target,"rwd");
        long n = BlockIO.numBlocks(in);
        for (int i=0;  i<n;  ++i) {
            BlockIO.writeBlock( out, i, BlockIO.readBlock(in,i,p), p );
            }
        in.close();
        out.setLength(out.getFilePointer());  // Truncate file at the current location.
        out.close();
        }
    static void copyFile( String src, String target ) throws IOException { copyFile(src,target,ParameterSet.DEFAULT); }

    /** A sample function which fills `target` with <em>two</em> copies of `src`, block-by-block (using `p.BLOCK_SIZE`).
     * It overwrites any existing file named `target`.
     */
    static void doubleFile( String src, String target, ParameterSet p ) throws IOException {
        RandomAccessFile in = new RandomAccessFile(src,"r");
        RandomAccessFile out = new RandomAccessFile(target,"rwd");
        long n = BlockIO.numBlocks(in);
        for (int i=0;  i<n;  ++i) {
            byte[] aBlock = BlockIO.readBlock(in,i,p);
            BlockIO.writeBlock( out, i, aBlock, p );
            BlockIO.writeBlock( out, n+i, aBlock, p );  // You can write past the current end-of-file -- it merely grows the file.
            }
        in.close();
        out.setLength(out.getFilePointer());  // Truncate file at the current location.
        out.close();
        }
    static void doubleFile( String src, String target ) throws IOException { doubleFile(src,target,ParameterSet.DEFAULT); }
    
    /** A sample function to create `fname` filled with some random characters,
     * making it `blockC0unt` long (as per `p.BLOCK_SIZE`).
     * The characters will be 7-bit ascii printable characters (hence each fitting in a single `byte`, and also <a href="https://en.wikipedia.org/wiki/UTF-8#Description">valid UTF-8</a>).
     * The sequence of characters generated depend on `p.rng`.
     * It overwrites any existing file named `fname`.
     */
    static void generateRandomFile( String fname, long numBlocks, ParameterSet p ) throws IOException {
        RandomAccessFile f = new RandomAccessFile(fname,"rwd");
        for (long i = 0;  i<numBlocks;  ++i) {
            BlockIO.writeBlock( f, i, randomChars(p.BLOCK_SIZE, p.rng), p );
            }
        f.setLength(f.getFilePointer());  // Truncate file at the current location.
        f.close();
        }
    static void generateRandomFile( String fname, long numBlocks ) throws IOException { generateRandomFile( fname, numBlocks, ParameterSet.DEFAULT ); }

    
    /** @return an array of `n` random characters, generated using `rng`.
     * The characters will be 7-bit ascii printable characters (hence each fitting in a single `byte`, and also <a href="https://en.wikipedia.org/wiki/UTF-8#Description">valid UTF-8</a>).
     */
    static byte[] randomChars( int n, java.util.Random rng ) {
        byte[] data = new byte[n];
        for (int i=0;  i<n;  ++i) { data[i] = (byte)(('a'-XTRA)+rng.nextInt(26+XTRA)); }
        return data;
        }
    static byte[] randomChars( int n ) { return randomChars(n, new Random(System.currentTimeMillis())); }
     
    private static int XTRA = 3;  // How many extra characters to use, in addition to 'a'..'z'.  There are 3 okay ones right before 'a'.
    
    
    /** Driver. */
    public static void main( String... __ ) throws IOException {
        ParameterSet.DEFAULT = new ParameterSet( 4096, 32 ); // block-size, record-size  (both in bytes)

        // Demo the sample functions in this class.
        Stats s = Stats.DEFAULT;
        String initialFile = "sample.txt";
        generateRandomFile( initialFile, 10 );
        displayFile(initialFile);
     //   copyFile("sample.txt", "sampleB.txt");
      //  doubleFile("sampleB.txt", "sample2.txt");
     //   doubleFile("sample2.txt", "sample4.txt");
     //   displayFile("sample4.txt");

        // Print stats (for whole program, in this case)
        System.out.println(s.toString());
        }
    }
/* #|
@author ibarland
@version 2018-Feb-27

@license: CC-BY 4.0 -- you are free to share and adapt this file
for any purpose, provided you include appropriate attribution.
    https://creativecommons.org/licenses/by/4.0/ 
    https://creativecommons.org/licenses/by/4.0/legalcode 
Including a link to the *original* file satisifies "appropriate attribution".
|# */
