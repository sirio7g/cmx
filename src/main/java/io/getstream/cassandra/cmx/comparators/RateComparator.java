package io.getstream.cassandra.cmx.comparators;

import io.getstream.cassandra.cmx.beans.ColumnFamilyLatency;

import java.util.Comparator;

public class RateComparator implements Comparator<ColumnFamilyLatency> {

    public enum RateType {
        ONE_MINUTE,
        FIVE_MINUTES,
        FIFTEEN_MINUTES;
    }

    private final RateType rateType;

    public RateComparator(final String type) {
        if (type != null) {
            if (type.equals(RateType.FIVE_MINUTES)) {
                this.rateType = RateType.FIVE_MINUTES;
            } else if (type.equals(RateType.FIFTEEN_MINUTES)) {
                this.rateType = RateType.FIFTEEN_MINUTES;
            } else {
                this.rateType = RateType.ONE_MINUTE;
            }
        } else {
            this.rateType = RateType.ONE_MINUTE;
        }
    }

    @Override
    public int compare(ColumnFamilyLatency o1, ColumnFamilyLatency o2) {
        switch (this.rateType) {
            case ONE_MINUTE:
                return Double.compare(o1.getOneMinuteRate(), o2.getOneMinuteRate()) * -1;
            case FIVE_MINUTES:
                return Double.compare(o1.getFiveMinuteRate(), o2.getFiveMinuteRate()) * -1;
            case FIFTEEN_MINUTES:
                return Double.compare(o1.getFifteenMinuteRate(), o2.getFifteenMinuteRate()) * -1;
        }
        throw new UnsupportedOperationException();
    }
}
