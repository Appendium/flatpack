package net.sf.flatpack;

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

    Record getRecord();
}
