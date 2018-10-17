

/* A file to demo the use of command-line-options's `allOptions`.
 * @author Ian Barland
 * @version 2016-mar-20
 */


/** Demonstrate how to use `CommandLineOption` and its methods `allOptions`.
 */
public class CommandLineOptionExample {

    /** The array of all options which this program can process. */
    static CommandLineOption[] options = {
         new CommandLineOption( "file",  'f',  false, "foo.txt", "the file to blazblarg" )
        ,new CommandLineOption( "name",  'n',  false, "ibarland", "the primary blazlbarger"  )
        ,new CommandLineOption( "size",  's',  false, "98", "how many blazzes to blarg (in dozens)" )
        ,new CommandLineOption( "verbose",  'v',  true, null, "run in verbose mode" )
        ,new CommandLineOption( "stuff", null, false, null, "what to call your stuff" )
        ,new CommandLineOption( "otherStuff", 'o', false, "blarg", "the help info for other stuff" )
        };

    /* After declaring the above, you can invoke the program with (say) 
     *    java CommandLineOptionExample --size 44 -f baz.txt
     * and then `allOptions` will return:
     *    { "baz.txt", "ibarland", "44", null, null, "blarg" }
     * Note that these values are in the order that you specify in your array-of-option_info.
     *
     * If the user includes a boolean flag: and then `allOptions` will include the string `"true"` (, not a boolean!) instead of `null`:
     *    java CommandLineOptionExample -v --size 44 -f baz.txt
     * returns
     *    { "baz.txt", "ibarland", "44", "true", null, "blarg" }
     */
  
    /** Just print the program's options (using any values from command-line arguments,
     * and the default-values specified in this file).
     */
    public static void main( String[] args ) {
        String[] settings = CommandLineOption.allOptions( args, options );
        // Now, the array `settings` contains all the options, in order:
        // either taken from the command-line, or from the default given in `options[]`.
    
        for (int i=0;  i<settings.length; ++i) {
            String quoteChar = (settings[i]!=null)  ?  "\""  :  "";  // don't put quotes around `null`.
            System.out.printf("Option #%d (%s) is %s%s%s.\n", i, options[i].longOption, quoteChar, settings[i], quoteChar );
            }

        }
        
    }
