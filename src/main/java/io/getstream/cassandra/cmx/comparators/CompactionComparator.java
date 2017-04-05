package io.getstream.cassandra.cmx.comparators;

import io.getstream.cassandra.cmx.beans.Compaction;

import java.util.Comparator;

public class CompactionComparator implements Comparator<Compaction> {

    public enum RateType {
        PENDING_COMPACTIONS,
        SSTABLE_COUNT;
    }

    private RateType rateType = RateType.PENDING_COMPACTIONS;;

    public CompactionComparator(final String type) {
        if (type.equals("SSTableCount")) {
            this.rateType = RateType.SSTABLE_COUNT;
        }
    }

    @Override
    public int compare(Compaction o1, Compaction o2) {
        switch (this.rateType) {
            case PENDING_COMPACTIONS:
                return Integer.compare(o1.getPending(), o2.getPending()) * -1;
            case SSTABLE_COUNT:
                return Integer.compare(o1.getSstableCount(), o2.getSstableCount()) * -1;
            default:
                return Integer.compare(o1.getPending(), o2.getPending()) * -1;
        }
    }
}
