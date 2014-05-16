package net.sf.flatpack;

public interface RecordDataSet {
    /**
     * Returns true if it has one more record. false if not
     *
     * @return boolean
     */
    boolean next();

    
    Record getRecord();
}
