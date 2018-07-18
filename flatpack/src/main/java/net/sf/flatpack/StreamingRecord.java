package net.sf.flatpack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StreamingRecord implements StreamingDataSet {
    private final DataSet dataSet;

    public StreamingRecord(final DataSet dataSet) {
        super();
        this.dataSet = dataSet;
    }

    @Override
    public Optional<Record> getRecord() {
        return dataSet.getRecord();
    }

    @Override
    public boolean next() {
        return dataSet != null && dataSet.next();
    }

    @Override
    public int getErrorCount() {
        return dataSet != null ? dataSet.getErrorCount() : 0;
    }

    @Override
    public List<DataError> getErrors() {
        return dataSet != null ? dataSet.getErrors() : Collections.emptyList();
    }
}
