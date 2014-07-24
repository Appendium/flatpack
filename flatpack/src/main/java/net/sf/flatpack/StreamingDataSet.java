package net.sf.flatpack;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * New with jdk8, define stream() methods. You should
 * start using this.
 *
 * @author Benoit Xhenseval
 * @since 3.4
 */
public interface StreamingDataSet extends RecordDataSet {
    /**
     * @since 4.0
     * @return a stream of Records
     */
    default Stream<Record> stream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(spliterator(), Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE), false);
    }

    /**
     * @since 4.0
     * @return a stream of Records
     */
    default Stream<Record> parallelStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(spliterator(), Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE), true);
    }

    default Iterator<Record> spliterator() {
        return new Iterator<Record>() {
            Optional<Record> nextData = Optional.empty();

            @Override
            public boolean hasNext() {
                if (nextData.isPresent()) {
                    return true;
                } else {
                    if (StreamingDataSet.this.next()) {
                        nextData = getRecord();
                    } else {
                        nextData = Optional.empty();
                    }
                    return nextData.isPresent();
                }
            }

            @Override
            public Record next() {
                if (nextData.isPresent() || hasNext()) {
                    final Record line = nextData.get();
                    nextData = Optional.empty();
                    return line;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }
}
