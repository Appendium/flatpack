package net.sf.pzfilereader.converter;

import java.math.BigDecimal;

import net.sf.pzfilereader.util.ParserUtils;

/**
 * Returns a BigInt
 * @author zepernick
 */
public class ConvertBigDecimal implements PZConverter{
    /*
     * (non-Javadoc)
     * 
     * @see net.sf.pzfilereader.converter#convertValue(java.lang.String)
     */
    public Object convertValue(String valueToConvert) {
        return new BigDecimal(ParserUtils.stripNonDoubleChars(valueToConvert));
    }
}
