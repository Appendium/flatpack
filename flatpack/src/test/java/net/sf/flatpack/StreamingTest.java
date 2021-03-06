package net.sf.flatpack;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import junit.framework.TestCase;

/**
 * JDK 8 Streaming test.
 * @author Benoit Xhenseval
 */
public class StreamingTest extends TestCase {

    private static class Test {
        private String itemName;
        private BigDecimal price;

        public String getItemName() {
            return itemName;
        }

        public void setItemName(final String itemName) {
            this.itemName = itemName;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(final BigDecimal price) {
            this.price = price;
        }
    }

    public void testContains() {
        final String cols = "item,price,purchaseDate\r\n"//
                + "MacBook,1890.20,20140523\r\n"//
                + "Surface3,850.00,20140524\r\n"//
        ;
        final Parser p = CsvParserFactory.newInMemoryParser(new StringReader(cols));
        final List<Test> ds = p.stream() //
                .map(t -> {
                    final Test r = new Test();
                    r.setItemName(t.getString("item"));
                    r.setPrice(t.getBigDecimal("price"));
                    return r;
                })// Mapping from Record to Test
                .filter(t -> "Surface3".equals(t.getItemName())) // only keep the Surface3 (why???)
                .collect(Collectors.toList());

        // test record 1 with Data in file!
        assertEquals("Size", 1, ds.size());
        final Test test = ds.get(0);
        assertEquals("Item", "Surface3", test.getItemName());
        assertTrue("Price", new BigDecimal("850").compareTo(test.getPrice()) == 0);
    }
}
