package io.getstream.cassandra.cmx.beans;

public class ColumnFamilyLatency {
    private String name;
    private Double oneMinuteRate;
    private Double fiveMinuteRate;
    private Double fifteenMinuteRate;
    private Double percentile50th;
    private Double percentile95th;
    private Double percentile99th;
    private Long count;
    private Long relativeCount;

    public ColumnFamilyLatency(String name, Double oneMinuteRate,
                               Double fiveMinuteRate,
                               Double fifteenMinuteRate,
                               Double percentile50th,
                               Double percentile95th,
                               Double percentile99th,
                               Long count) {
        this.name = name;
        this.oneMinuteRate = oneMinuteRate;
        this.fiveMinuteRate = fiveMinuteRate;
        this.fifteenMinuteRate = fifteenMinuteRate;
        this.percentile50th = percentile50th;
        this.percentile95th = percentile95th;
        this.percentile99th = percentile99th;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getOneMinuteRate() {
        return oneMinuteRate;
    }

    public void setOneMinuteRate(Double oneMinuteRate) {
        this.oneMinuteRate = oneMinuteRate;
    }

    public Double getFiveMinuteRate() {
        return fiveMinuteRate;
    }

    public void setFiveMinuteRate(Double fiveMinuteRate) {
        this.fiveMinuteRate = fiveMinuteRate;
    }

    public Double getFifteenMinuteRate() {
        return fifteenMinuteRate;
    }

    public void setFifteenMinuteRate(Double fifteenMinuteRate) {
        this.fifteenMinuteRate = fifteenMinuteRate;
    }

    public Double getPercentile50th() {
        return percentile50th;
    }

    public void setPercentile50th(Double percentile50th) {
        this.percentile50th = percentile50th;
    }

    public Double getPercentile95th() {
        return percentile95th;
    }

    public void setPercentile95th(Double percentile95th) {
        this.percentile95th = percentile95th;
    }

    public Double getPercentile99th() {
        return percentile99th;
    }

    public void setPercentile99th(Double percentile99th) {
        this.percentile99th = percentile99th;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getRelativeCount() {
        return relativeCount;
    }

    public void setRelativeCount(Long relativeCount) {
        this.relativeCount = relativeCount;
    }
}
