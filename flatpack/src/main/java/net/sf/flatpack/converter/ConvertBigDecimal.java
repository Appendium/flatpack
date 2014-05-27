package net.sf.flatpack.converter;

import java.math.BigDecimal;

import net.sf.flatpack.util.ParserUtils;

/**
 * Returns a BigDecimal
 * Non numeric chars are removed from the string
 * before converting
 *
 * @author Paul Zepernick
 */
public class ConvertBigDecimal implements Converter {
    /*
     * (non-Javadoc)
     *
     * @see net.sf.flatpack.converter#convertValue(java.lang.String)
     */
    @Override
    public Object convertValue(final String valueToConvert) {
        return new BigDecimal(ParserUtils.stripNonDoubleChars(valueToConvert));
    }
}
