import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

// To make a class that gets a free constructor, equals, hash, toString for immutable data:
// 
//
// class Lala extends ObjectIan {
//   int n;
//   String s;   // default-initialized fields won't work :-(
//  
//   /** A constructor which assigns each arg
//    * to the fields in alphabetical order.
//    * Must be same number of args as fields.
//    */
//   Lala( Object... args ) { super(args); }
//   Lala( int n ) { super(n,"hmm"); }
//   Lala( String _s ) { 
//       this.s = _s;  
//       this.n = _s.length();
//       }
//
//   public static void main( String[] args ) {
//      // Demo the constructor:
//      Lala la1 = new Lala( 3, "hello" );
//      Lala la2a = new Lala( 5, "bye" );
//      Lala la2b = new Lala( 5, "bye" );
//      Lala la3 = new Lala( 5 );
//    
//      // Demo toString:
//      System.out.println( "la1:  " + la1.toString() );
//      System.out.println( "la2a: " + la2a.toString() );
//      System.out.println( "la2b: " + la2b.toString() );
//      System.out.println( "la3:  " + la3.toString() );
// 
//      System.out.println( "      " + new Lala("supercalifragilistic!") );
// 
//      // Demo equals:
//      System.out.println( "la1  equals la2a: " + la1.equals(la2a) );
//      System.out.println( "la2a equals la2b: " + la2a.equals(la2b) );
//      System.out.println( "la2a   ==   la2b: " + (la2a == la2b) );
//      }
//     
//      // todo: Demo hashmap retrieval
//    }
   
  



/** A boilerplate constructor, equals, hashcode, toString  useful for many classes.
 * (The constructor takes args for each field, // in (oddly) alphabetical order;
 *  toString prints back the way that you'd call the constructor.
 *  hashcode presumes the object is immutable.)
 * @author Ian Barland
 * @version 2018.Feb.17
 */
abstract class ObjectIan {

  /** Constructor:
   * Initialize each field with the provided args,
   * in the declared order.
   */
  public ObjectIan( Object... args ) {
      
    // TODO: for efficiency, keep a static Map<Class<? extends ObjectIan>,List<Field>> instanceFields.
    instanceFields = new ArrayList<Field>();
    for (Field f : this.getClass().getDeclaredFields()) {
      /* N.B. The docs for getDeclaredFields don't explicitly guarantee that two calls
       * on the same Class will return the fields in the same order.  I can't imagine
       * that an implementation *wouldn't* do that, but perhaps sort it just in case?
       */
      if (!Modifier.isStatic(f.getModifiers())) { instanceFields.add(f); }
      }

    // HACK -- allow the subclass to provide their own constructor (at their own risk), via `super(null)`.
    if (args==null) return; 

    /*
    String msg2 = "Debug: ObjectIan constructor: <";
    for (Field f : instanceFields) {
        try { msg2 += String.format( "%s %s = %s, ", f.getType().getName(), f.getName(), f.get(this) ); }
        catch (IllegalAccessException iae) { throw new RuntimeException(iae); }
        }
    msg2 +=  ">\n";
    System.err.printf(msg2);
    */
    
    // We could declare Object... args, but we'll stress how our method is abstract:
    // The subclass must have a varargs constructor which calls 'super(args)'.
    //
    String subclassName = this.getClass().getName();  // For use in diagnostic messages.
    if (!Character.isUpperCase( subclassName.charAt(0) )) {
      System.err.print( "Warning:"
                      + " Class name `" + subclassName + "`"
                      + " should start with upper case, by convention."
                      + " --  ObjectIan super." );
      }
    
    if (args.length != this.instanceFields.size()) {
      String msg = String.format("Must provide %d args to constructor (not %d)", this.instanceFields.size(), args.length );
      msg += String.format(" `new %s(", subclassName);
      boolean needComma = false;
      for (Field f : this.instanceFields) {
        if (Modifier.isStatic(f.getModifiers())) continue;
        msg += String.format( "%s[%s]", (needComma  ?  ", "  :  ""),  f.getName() );
        needComma = true;
        }
      msg += ")`.";
      throw new IllegalArgumentException( "ObjectIan super: " + msg);
      }
      

    //Arrays.sort(fields, compareFieldsAlphByName );
    int i = 0;
    for (Field f : this.instanceFields) {
      if (Modifier.isStatic(f.getModifiers())) continue;
      try {
        f.setAccessible(true);
        f.set(this, args[i] );
        ++i;
        }
      catch(IllegalAccessException e) {
        throw new RuntimeException( "ObjectIan super: "
                                  + "Security manager doesn't allow accessing fields through reflection. "
                                    + "You'll have to write the " + subclassName + " constructor yourself.\n"
                                  + e.toString() );
        }
      catch(IllegalArgumentException e) {
        throw new RuntimeException( "ObjectIan super: "
                                  + "Can't initialize field " + subclassName + "." + f.getName()
                                  + " with value " + args[i].toString()
                                  + " (of type " + args[i].getClass().getName() + ").\n"
                                  + e.toString() );
        }
      /*
      if (!Character.isLowerCase(f.getName().charAt(0))) {
         System.err.println( "Warning:"
                           + " Field name `" +  f.getName() + "` in class `" + subclassName + "`"
                           + " should start with lower case, by convention."
                           + " --  ObjectIan super." );
        }
      */
      }
    
    }


   @Override
   /** A generic hash, summing the hash of each field.
    *  This version ASSUMES THE OBJECT IS IMMUTABLE,
    *  caching the result in the field 'hash'.
    *  For mutable objects, remove the 'if hash==0' guard.
    */
   public int hashCode() {
     if (hash == 0) {
       for (Field f : this.instanceFields) {
         try {
           f.setAccessible(true);
           hash *= Math.pow(2,5)-1;  // shuffle the bits (well?). // TODO: 2,5 as named constants.
           hash += f.get(this).hashCode();
           }
         catch(IllegalAccessException e) {
           throw new RuntimeException( "Rats, I can't access the field through reflection. "
                                     + "Try overriding `hashCode` in " + this.getClass().getName() + "\n"
                                     + e.toString() );
           }
         }
       }
     return hash;
     }
   private int hash = 0;

   @Override
   /** A deep equals check: call equals on each field.
    */
   public boolean equals( Object oth ) {
    if ( this == oth ) {
      return true;
      }
    else if ( oth == null || (this.getClass() != oth.getClass()) ) {
      return false;
      }
    else {
      for (Field f : this.instanceFields) {
        try {
          f.setAccessible(true);
          boolean thisNull = (f.get(this)==null);
          boolean thatNull = (f.get(oth )==null);
          if      ( thisNull &&  thatNull) { /* continue */ }
          else if ( thisNull && !thatNull) return false;
          else if (!thisNull &&  thatNull) return false;
          else if (! f.get(this).equals(f.get(oth))) return false;
          else { /* continue */ }
          }        catch(IllegalAccessException e) {
          throw new RuntimeException( "Rats, I can't access the field through reflection. "
                                    + "Try overriding `equals` in " + this.getClass().getName() + "\n"
                                    + e.toString() );
          }
        }
      return true;
      }
    }
    
  @Override
  public String toString() {
    return this.toString(false);
    }
  
  /** Return a toString which looks close to a constructor call:
   * @param includeFieldNames If true, include
   * 
   */
  public String toString( boolean includeFieldNames ) {
    StringBuilder str = new StringBuilder();
    str.append("new " + this.getClass().getName() + "( ");
    boolean needComma = false;  // Need to insert a comma before the next field?
    for (Field f : this.instanceFields) {
      try {
        if (needComma) str.append( ", " );
        Object theVal = f.get(this);
        if (includeFieldNames) str.append( f.getName().toString() ).append("=");
        if (quoteMarks.get(theVal.getClass()) != null) {
          str.append( quoteMarks.get(theVal.getClass()) );
          }
        if (theVal.getClass().isInstance(ObjectIan.class)) {
          str.append(  ObjectIan.class.cast(theVal).toString(includeFieldNames));
          }
        else {
          str.append( theVal.toString());
          }
        if (quoteMarks.get(theVal.getClass()) != null) {
          str.append( quoteMarks.get(theVal.getClass()) );
          }
        needComma = true;
        }
      catch(IllegalAccessException e) {
        throw new RuntimeException( "Rats, I can't access the field through reflection. "
                                  + "Try overriding `toString` in " + this.getClass().getName() + "\n"
                                  + e.toString() );
        }
      }
    str.append( " )" );
    return str.toString();
    }


  /** When toString'ing fields, put quotes around certain types (Strings, chars, etc). */
  private static Map<Class,String> quoteMarks = new HashMap<Class,String>();
  static {
   quoteMarks.put( Character.class,     "'" );
   quoteMarks.put( String.class,        "\"" );
   quoteMarks.put( StringBuffer.class,  "\"" );
   quoteMarks.put( StringBuilder.class, "\"" );
   }
   
    
  // In case we want to sort the fields alphabetically:
  private static Comparator<Field> compareFieldsAlphByName = new Comparator<Field>() {
    @Override
    public int compare(Field f1, Field f2) { return f1.getName().compareTo( f2.getName() ); }
    /* We could upcase the field name, but that'd be ambiguous if a beginner were to
     * have two field names that differed only by case.
     * Better to make them aware of the difference.
     */
    };
  
  List<Field> instanceFields;  // The (non-static) fields in the subclass.

  
  

  }
  /* Deficincies of this class:
   * Fundamental:
   *   final fields won't compile -- not init'd.
   *   a (sub)class can't provide initial/default field values,
   *     because (a) this class can't tell, and (x) the jvm
   *     will set those fields *after* this superconstructor completes,
   *     *overwriting* any assignments made here.
   *     ...The best solution is just to force the caller to
   *     write their own constructor, if they want;
   *     we allow `super(null)` to bypass our superconstructor.
   * 
   *   Can't generate setters (only a "set")
   * To fix/patch:
   * - have ObjectMutable, ObjectImmutable ?
   * 
   */
/* #|
@author ibarland
@version 2018-Feb-18

@license: CC-BY 4.0 -- you are free to share and adapt this file
for any purpose, provided you include appropriate attribution.
    https://creativecommons.org/licenses/by/4.0/ 
    https://creativecommons.org/licenses/by/4.0/legalcode 
Including a link to the *original* file satisifies "appropriate attribution".
|# */
