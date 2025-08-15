package net.sf.flatpack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * JDK 8 Streaming test.
 * @author Benoit Xhenseval
 */
class StreamingTest {

    private static class TestObject {
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

    @Test
    void testContains() {
        final String cols = "item,price,purchaseDate\r\n"//
                + "MacBook,1890.20,20140523\r\n"//
                + "Surface3,850.00,20140524\r\n"//
        ;
        final Parser p = CsvParserFactory.newInMemoryParser(new StringReader(cols));
        final List<TestObject> ds = p.stream() //
                .map(t -> {
                    final TestObject r = new TestObject();
                    r.setItemName(t.getString("item"));
                    r.setPrice(t.getBigDecimal("price"));
                    return r;
                })// Mapping from Record to Test
                .filter(t -> "Surface3".equals(t.getItemName())) // only keep the Surface3 (why???)
                .collect(Collectors.toList());

        // test record 1 with Data in file!
        assertEquals(1, ds.size(), "Size");
        final TestObject test = ds.get(0);
        assertEquals("Surface3", test.getItemName(), "Item");
        assertTrue(new BigDecimal("850").compareTo(test.getPrice()) == 0, "Price");
    }
}
