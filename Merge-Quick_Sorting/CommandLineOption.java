import java.util.*;

/** CommandLineOptions: Support for processing options provided on the command-line.
 *
 * If callers provide named-arguments on the command-line in any order,
 * then your program can call `allOptions` to get back an array of ALL the specified option-values,
 * with predefined defaults used for those the caller didn't specify.
 *
 *  Example: If you want the caller to be able to optionally provide any of the
 *  command-line options `--file`, `--name` and `--size`, then add the following
 *  to your program:
 *
 *  static CommandLineOption[] options = {
 *       new CommandLineOption( "file",  'f',  false, "foo.txt", "the file to blazblarg" )
        ,new CommandLineOption( "name",  'n',  false, "ibarland", "the primary blazlbarger"  )
 *      ,new CommandLineOption( "verbose",  'v',  true, null, "run in verbose mode" )
 *      ,new CommandLineOption( "size",  's',  false, "98", "how many blazzes to blarg (in dozens)" )
 *      };
 *
 *  The pieces of info you must provide for each option are:
 *    long-version (`--name`), short-version (`-n`), is a boolean option?, a default value if not provided ("ibarland"),
 *    and a string which might someday be used in a help-message (but is not currently used).
 *    (For boolean options, the default value must be either `null` or `"false"` or `""`.)
 * 
 * After declaring the above, you can invoke the program with (say) 
 *    java CommandLineOptionExample --size 44 -f baz.txt
 * and then `allOptions` will return:
 *    { "baz.txt", "ibarland", null, "44" }
 * Note that these values are in the order that you specify in your array-of-option_info.
 *
 * If the user includes a boolean flag: and then `allOptions` will include the string `"true"` (, not a boolean!) instead of `null`:
 *    java CommandLineOptionExample -v --size 44 -f baz.txt
 * returns
 *    { "baz.txt", "ibarland", "true", "44" }
 *
 *
 * 
 * See also: ...there must be other more robust libraries out there.
 *    I'm making this to be parallel to the C-language version I wrote, which 
 *    I wanted to be lighter-weight than the C libraries I found.
 *
 * Known bugs:
 * - We assume every option is followed by a value!  (No 'binary' flag-options.)
 * - If a value looks like an option, this code will get confused (e.g. if you
 * try to specify a --file whose name is "--size", or if the name of the
 * executable argv[0] is "-n", etc.)!
 */
class CommandLineOption extends ObjectIan { // ObjectIan provides boilerplate constructor, equals, hashcode.
    /** How to specify this option on the command line (preceded by the two chars `"--"`). */
    String longOption;
    /** How to specify this option on the command line (preceded by the one char `"-"`). */
    Character shortOption;
    /** Is this option an on/off (boolean) sort, as opposed to having an associated value? */
    boolean isBooleanOption;
    /** The default value for this option, if not provided on the command line.
     *  This field doesn't make sense for booleans (which are always off(`null`) or on(`"true"`).
     *  For boolean options, initialize this to `null` (though `""` and `"false"` also accepted).
     */
    String defaultValue;
    /** A short, one-line summary of what this option means to the program. */
    String helpString;

    CommandLineOption( Object... args ) {  // Call with 5 values for the above 5 fields.
        super(args); 
        if (this.isBooleanOption && !ALLOWED_BOOLEAN_DEFAULTS.contains( this.defaultValue instanceof String  ?  (this.defaultValue).toLowerCase() : this.defaultValue )) {
            throw new IllegalArgumentException(String.format("A boolean CommandLineOption's default must be one of %s.", ALLOWED_BOOLEAN_DEFAULTS));  // todo: add quotes around the `"true"`,`"false"` printed in this message.
            }
        }
    private static List<String> ALLOWED_BOOLEAN_DEFAULTS = Arrays.asList("false","",(String)null);
    /*CommandLineOption( String _longOption, char _shortOption, boolean _isBooleanOption, String _defaultValue, String _helpString ) {
        this.longOption = _longOption;
        this.shortOption = _shortOption;
        this.isBooleanOption = _isBooleanOption;
        this.defaultValue = _defaultValue;
        this.helpString = _helpString;
        }
    */

    /* Given a string of form "--otherStuff", return the otherStuff.
     * If it wasn't of that form, return null.
     */
    /*private*/ static String extractLongOptionName( String arg ) {
        if (arg==null || arg.length() <= 2 || !arg.substring(0,2).equals("--")) 
            return null;
        else
            return arg.substring(2);
        }
 
                          
    /* Given a string of form "-someChar", return the char.
     * If it wasn't of that form, return '\0'.
     */
    /*private*/ static Character extractShortOptionName( String arg ) {
        if (arg==null || arg.length() != 2 || arg.charAt(0) != '-' || arg.equals("--")) {
            return null;
            }
        else {
            return arg.charAt(1);
            }
        }
                           
                           
    /* @return the option's value, taken from the command line:
     * Usually the *next* word in `haystack[]` after the option's short- or long-name,
     * or the default value (if not present).
     * For boolean options, either `null` or the string `"true"` is returned.
     * If multiple occurrences of the option, we take the last one ('overwriting' previous ones).
     * In all cases, if haystack contains `"--"`, we won't search beyond that.
     */
    /*private*/ static String findOption( final CommandLineOption target, final String haystack[] ) {
        String answerSoFar = (!target.isBooleanOption)  
                           ? target.defaultValue
                           : null;

        for (int i=0;  i < haystack.length-1;  ++i) {
            if (haystack[i].equals("--")) break;  /* "--" stops option-processing */
            String    asLongOption  = extractLongOptionName( haystack[i]);
            Character asShortOption = extractShortOptionName(haystack[i]);
            if (   (asLongOption  != null &&  asLongOption.equals(target.longOption))
                || (asShortOption != null && asShortOption.equals(target.shortOption))) {
                if (target.isBooleanOption) {
                    answerSoFar = "true";
                    }
                else {
                    answerSoFar = haystack[i+1];
                    ++i;  // skip over haystack[i+1] as a potential next-arg.
                    }
                }
            }
        return answerSoFar;
        }


    /* If `arg` looks like it's an option, is it one of the allowed ones in `options`?
     */
    /*private*/ static boolean apparentOptionIsLegal( CommandLineOption[] options, String arg ) {
        String asLongOption  = extractLongOptionName(  arg );
        Character  asShortOption = extractShortOptionName( arg );
        if (asLongOption==null && asShortOption==null) return true; 
        /* Doesn't look like it's trying to be an option, so no problem. */
    
        for (int i=0;  i< options.length;  ++i) {
            if (asLongOption!=null) {
                if (asLongOption.equals( options[i].longOption )) return true;
                }
            else if (asShortOption!=null) {
                if (asShortOption==options[i].shortOption) return true;
                }
            else {
                throw new RuntimeException( "Whoopsie, this line should be unreachable!" );
                }
            }
        return false;
        }




    /* @return an array with the values for ALL options in `options`,
     * based on `argv`.
     * The strings are taken from the command-line if provided, else from `options[i].default`
     * So if <tt>argv == { "--size", "27", "-v", "-f", "foo.txt" }</tt>
     * and options was an array with the options<pre>
       { new CommandLineOption("file","f",false,"-","…"), 
         new CommandLineOption("name",'n',false,"ibarland","…"), 
         new CommandLineOption("size",'s',false,"48","…")
         new CommandLineOption("verbose",'v',true,null,"…") }
     </pre>
     * then we'd return {"foo.txt", "ibarland", "27", "true"}.
     */
    public static String[] allOptions( String[] argv, CommandLineOption[] options ) {
        String[] allOpts = new String[ options.length ];
        for (int i=0;  i<options.length;  ++i) {
            allOpts[i] = findOption( options[i], argv );
            }

        // Also: make sure that everything that LOOKS like an arg is valid:
        for (int i=0;  i<argv.length;  ++i) {
            if (argv[i].equals("--")) break;
            if (!apparentOptionIsLegal(options, argv[i])) {
                System.err.printf("Warning: argument #%d, \"%s\", is not a known option.\n", i, argv[i]);
                }
            }
  
        return allOpts;
        }
  

    }
