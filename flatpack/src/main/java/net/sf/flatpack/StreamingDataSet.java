package net.sf.flatpack;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Future interface which, under jdk8, will define stream() methods.  You should start using this.
 * @author Benoit Xhenseval
 * @since 3.4
 */
public interface StreamingDataSet extends RecordDataSet {
    default Stream<Record> stream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(spliterator(), Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE), false);
    }

    default Stream<Record> parallelStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(spliterator(), Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE), true);
    }

    default Iterator<Record> spliterator() {
        return new Iterator<Record>() {
            Record nextData = null;

            @Override
            public boolean hasNext() {
                if (nextData != null) {
                    return true;
                } else {
                    if (StreamingDataSet.this.next()) {
                        nextData = getRecord();
                    } else {
                        nextData = null;
                    }
                    return nextData != null;
                }
            }

            @Override
            public Record next() {
                if (nextData != null || hasNext()) {
                    final Record line = nextData;
                    nextData = null;
                    return line;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }
}
