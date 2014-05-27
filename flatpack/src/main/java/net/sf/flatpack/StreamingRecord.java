package net.sf.flatpack;

public class StreamingRecord implements StreamingDataSet {
    private final DataSet dataSet;

    public StreamingRecord(final DataSet dataSet) {
        super();
        this.dataSet = dataSet;
    }

    @Override
    public Record getRecord() {
        return dataSet.getRecord();
    }

    @Override
    public boolean next() {
        return dataSet != null ? dataSet.next() : false;
    }
}
