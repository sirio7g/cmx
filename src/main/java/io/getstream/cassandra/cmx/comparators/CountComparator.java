package io.getstream.cassandra.cmx.comparators;

import io.getstream.cassandra.cmx.beans.ColumnFamilyLatency;

import java.util.Comparator;

public class CountComparator implements Comparator<ColumnFamilyLatency> {

    @Override
    public int compare(ColumnFamilyLatency o1, ColumnFamilyLatency o2) {
        return Long.compare(o1.getCount(), o2.getCount()) * -1;
    }

}
