package net.sf.flatpack;

import java.util.Collections;
import java.util.List;

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

    @Override
    public int getErrorCount() {
        return dataSet != null ? dataSet.getErrorCount() : 0;
    }

    @Override
    public List getErrors() {
        return dataSet != null ? dataSet.getErrors() : Collections.emptyList();
    }
}
