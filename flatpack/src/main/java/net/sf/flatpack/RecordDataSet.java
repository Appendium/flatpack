package net.sf.flatpack;

import java.util.List;
import java.util.Optional;

/**
 * Rather than treating a DataSet as a stateful class whereby we need to extract each column one by
 * one, this interface allows you to extract a record at a time.
 * @since 3.4
 */
public interface RecordDataSet {
    /**
     * Returns true if it has one more record. false if not
     *
     * @return boolean
     */
    boolean next();

    Optional<Record> getRecord();

    /**
     * Returns A Collection Of DataErrors that happened during processing
     *
     * @return Vector
     */
    List<DataError> getErrors();

    /**
     * Returns total number of records which contained a parse error in the
     * file.
     *
     * @return int - Record Error Count
     */
    int getErrorCount();

}
