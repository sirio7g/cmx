package io.getstream.cassandra.cmx.beans;

public class Compaction {

    private String name;
    private int pending = 0;
    private int sstableCount = 0;

    public Compaction(String name, int pending, int sstableCount) {
        this.name = name;
        this.pending = pending;
        this.sstableCount = sstableCount;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public int getSstableCount() {
        return sstableCount;
    }

    public void setSstableCount(int sstableCount) {
        this.sstableCount = sstableCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Compaction that = (Compaction) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
