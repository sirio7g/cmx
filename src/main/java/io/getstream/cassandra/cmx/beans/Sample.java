package io.getstream.cassandra.cmx.beans;

import java.math.BigDecimal;

public class Sample {

    protected Double mean;
    protected Integer count;

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double diff(Sample sample) {
        return new BigDecimal(((this.count * this.mean) - (sample.getCount() * sample.getMean())) / (this.getCount() - sample.getCount()) / 1000)
                .setScale(3, BigDecimal.ROUND_UP).doubleValue();
    }

    public int diffCount(Sample sample) {
        return this.count - sample.getCount();
    }
}