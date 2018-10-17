/* #|
@author saustin4

@version 2018-April-10

@sources  http://www.java2novice.com/java-sorting-algorithms/merge-sort/
		  https://stackoverflow.com/questions/13727030/mergesort-in-java
		  https://www.sanfoundry.com/java-program-implement-merge-sort/
		  collaborated with James Caldwell on concepts and structures
		  

@license: CC-BY 4.0 -- you are free to share and adapt this file
for any purpose, provided you include appropriate attribution.
    https://creativecommons.org/licenses/by/4.0/ 
    https://creativecommons.org/licenses/by/4.0/legalcode 
Including a link to the *original* file satisfies "appropriate attribution".
|# */

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

    static CommandLineOption[] options = {
            new CommandLineOption( "file",  'f',  false, null, "starter file" )
            ,new CommandLineOption( "block-size",  'b',  false, "4096", "size of blocks" )
            ,new CommandLineOption( "record-size",  'k',  false, "32", "size of records" )
            ,new CommandLineOption( "max-memory",  'M',  false, "3", "memory limit" )
            ,new CommandLineOption( "out", 'o', false, null, "final file name" )

    };

   
    public static void main(String[] args) {

        String[] settings = CommandLineOption.allOptions( args, options );


        // This is for Command Line use
        //  new Xsort(settings[0],Integer.parseInt(settings[1]),Integer.parseInt(settings[2]),settings[4]); //uncomment for command line use


        // For testing without command line, input file needs to be outside of src folder and use the below command instead
        // For command line, put inside of src folder and specify input file name and finalLoad name if desired
        new Xsort("rand1e4blocks.txt",4096,32,"finalLoad.txt");


        // comment out for command line debugging
        //try {Demo.displayFile(settings[0]); } catch (IOException e) {  e.printStackTrace(); }

       //Leave this uncomment for IDE testing
        try {Demo.displayFile("finalLoad.txt"); } catch (IOException e) {  e.printStackTrace(); }
        Stats s = Stats.DEFAULT;
        System.out.println(s.toString());
    }
}
