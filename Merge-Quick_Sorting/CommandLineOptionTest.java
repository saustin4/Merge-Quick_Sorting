import static org.junit.Assert.*;
import org.junit.Test;

/** Unit tests. */
public class CommandLineOptionTest {

    private static int testCount=0;
    private static int failCount=0;
    private static int currColumn=0;
    private static void printTstMsg(String msg) {
        currColumn=0;
        //System.err.printf("\n%s\n", msg);
        }
        
    private static void printTstSummary() {
        printTstMsg( String.format("Completed %d tests (%d failed).", testCount, failCount) );
        }
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

    @Test
    public void testExtractLongOptionName(){
        printTstMsg("ExtractLongOptionName");
        assertEquals( "hello", CommandLineOption.extractLongOptionName("--hello"));
        assertEquals( null, CommandLineOption.extractLongOptionName("noLeadingDashes"));
        assertEquals( null, CommandLineOption.extractLongOptionName("-hello"));
        assertEquals( null, CommandLineOption.extractLongOptionName("-h"));
        assertEquals( null, CommandLineOption.extractLongOptionName("--"));
        assertEquals( null, CommandLineOption.extractLongOptionName(""));
        assertEquals( null, CommandLineOption.extractLongOptionName(null));
        }

    @Test
    public void testExtractShortOptionName(){
        printTstMsg("ExtractShortOptionName");
        assertEquals( new Character('h'), CommandLineOption.extractShortOptionName("-h"));
        assertEquals( null, CommandLineOption.extractShortOptionName("h"));
        assertEquals( null, CommandLineOption.extractShortOptionName("hello"));
        assertEquals( null, CommandLineOption.extractShortOptionName("--hello"));
        assertEquals( null, CommandLineOption.extractShortOptionName("h-"));
        assertEquals( null, CommandLineOption.extractShortOptionName("-"));
        assertEquals( null, CommandLineOption.extractShortOptionName("--"));
        assertEquals( null, CommandLineOption.extractShortOptionName(""));
        assertEquals( null, CommandLineOption.extractShortOptionName(null));
        }



    @Test
    public void testfindOption() {
        printTstMsg("testFindOption");
        String[][] samples = { {}
                             , { "--" }
                             , {"--hello","tag", "--hello","tag", "--isYummy", "-x","99", "--", "--hello", "tag2" }
                             , {"--hello","tag", "--", "--hello", "tag1" }
                             , { "--hello","tag", "--isYummy","", "-x","99", "--", "--hello", "tag2" }
                             , { "--hello","tag", "-x","33", "--hello", "tag2" }
                             , { "-y", "-m", "-n", "--hello","tag" }
                             };
        CommandLineOption[] options = {
            new CommandLineOption( "hello", 'h', false, "ibarland", "the name of the package-author" ),
            new CommandLineOption( "bye", 'b', false, "99", "the size of the frobzat, in meters." ),
            new CommandLineOption( "isNice",  'n', true, "false", "is this a nice  frozbat?" ),
            new CommandLineOption( "isYummy", 'y', true, "",      "is this a yummy frozbat?" ),
            new CommandLineOption( "isMean",  'm', true, null,    "is this a mean  frozbat?" ),
            };
        assertEquals( "ibarland", CommandLineOption.findOption( options[0], samples[0] ));
        assertEquals( "ibarland", CommandLineOption.findOption( options[0], samples[1] ));
        assertEquals( "tag", CommandLineOption.findOption( options[0], samples[2] ));

        assertEquals( "99", CommandLineOption.findOption( options[1], samples[0] ));
        assertEquals( "33", CommandLineOption.findOption( options[1], samples[5] ));

        assertEquals( null, CommandLineOption.findOption( options[2], samples[0] ));
        assertEquals( null, CommandLineOption.findOption( options[3], samples[0] ));
        assertEquals( null, CommandLineOption.findOption( options[4], samples[0] ));
        assertEquals( "true", CommandLineOption.findOption( options[2], samples[6] ));
        assertEquals( "true", CommandLineOption.findOption( options[3], samples[6] ));
        assertEquals( "true", CommandLineOption.findOption( options[4], samples[6] ));

        assertEquals( "true", CommandLineOption.findOption( options[3], samples[2] ));  // boolean flag present, long option
        }


    @Test
    public void testApparentOptionIsLegal() {
        printTstMsg("testApparentOptionIsLegal");
        CommandLineOption[] options = {
            new CommandLineOption( "name", 'n', false, "ibarland", "the name of the package-author" ),
            new CommandLineOption( "size", 's', false, "45", "the size of the frobzat, in meters." ),
            new CommandLineOption( "isFun", 'f', true, null, "the size of the frobzat, in meters." ),
            };
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "--name" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "--size" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "-s" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "-n" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "blah" ));
        assertEquals(  false, CommandLineOption.apparentOptionIsLegal( options, "--zasd" ));
        assertEquals(  false, CommandLineOption.apparentOptionIsLegal( options, "-z" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "--" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "-f" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "--isFun" ));
        }

    /** Run the other tests. */
    private static void main( String... args ) {
        CommandLineOptionTest thiss = new CommandLineOptionTest();
        thiss.testExtractLongOptionName();
        thiss.testExtractShortOptionName();
        thiss.testfindOption();
        thiss.testApparentOptionIsLegal();
        printTstSummary();
        }

    }
