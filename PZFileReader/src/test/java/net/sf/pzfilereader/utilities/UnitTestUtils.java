package net.sf.pzfilereader.utilities;

/**
 * Static utility methods for Junit tests
 * 
 * @author Paul Zepernick
 */
public class UnitTestUtils {
    
    /**
     * Builds a delimited qualified string containing the elements passed in
     * 
     * @param elements
     *             Elements To Include In Delimited String
     * @param delimiter
     * @param qualifier
     * @return String
     */
    public static String buildDelimString(final String[] elements, final char delimiter, final char qualifier){
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < elements.length; i++){
            if (i > 0){
                sb.append(delimiter);
            }
            sb.append(qualifier).append(elements[i]).append(qualifier);
        }
        
        
        return sb.toString();
    }
    
    /** Builds a delimited string containing the elements passed in
    * 
    * @param elements
    *             Elements To Include In Delimited String
    * @param delimiter
    * @return String
    */
   public static String buildDelimString(final String[] elements, final char delimiter){
       final StringBuffer sb = new StringBuffer();
       for (int i = 0; i < elements.length; i++){
           if (i > 0){
               sb.append(delimiter);
           }
       }
       
       
       return sb.toString();
   }
}
