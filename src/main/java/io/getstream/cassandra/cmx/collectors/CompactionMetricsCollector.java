package io.getstream.cassandra.cmx.collectors;

import io.getstream.cassandra.cmx.beans.Compaction;
import io.getstream.cassandra.cmx.comparators.CompactionComparator;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.format;

public class CompactionMetricsCollector implements MetricsCollector {

    private final String sort;
    private final int limit;
    private final String keyspace;

    private Set<String> allowedColumnFamilies;

    public CompactionMetricsCollector(String keyspace,
                                   Set<String> allowedColumnFamilies,
                                   int limit,
                                   String sort) {
        this.keyspace = keyspace;
        this.allowedColumnFamilies = allowedColumnFamilies;
        this.limit = limit;
        this.sort = sort;
    }

    @Override
    public void collect(MBeanServerConnection serverConnection) throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException, IOException, AttributeNotFoundException, MBeanException {
        printGeneralCompactionStats(serverConnection);
        printCompactionPerColumnFamilies(serverConnection);
    }

    private void printCompactionPerColumnFamilies(MBeanServerConnection serverConnection) throws MalformedObjectNameException, InstanceNotFoundException, IOException, ReflectionException, AttributeNotFoundException, MBeanException {
        System.out.println(format("%30s %12s %12s", "Name", "Pending", "SSTable count"));

        int i = 0;
        for (Compaction compaction : collectCompactionMetrics(serverConnection)) {
            System.out.println(format("%30s %12d %12d",
                    compaction.getName(),
                    compaction.getPending(),
                    compaction.getSstableCount()
            ));
            if (++i >= this.limit) break;
        }
        System.out.println("\n");
    }

    private void printGeneralCompactionStats(MBeanServerConnection serverConnection) throws MalformedObjectNameException, InstanceNotFoundException, IOException, ReflectionException, AttributeNotFoundException, MBeanException {
        Integer pending = (Integer)serverConnection.getAttribute(
                new ObjectName("org.apache.cassandra.metrics:type=Compaction,name=PendingTasks"), "Value");
        List<Attribute> attributes = serverConnection.getAttributes(
                new ObjectName("org.apache.cassandra.metrics:type=Compaction,name=TotalCompactionsCompleted"),
                new String[]{"Count", "OneMinuteRate", "FiveMinuteRate", "FifteenMinuteRate", "MeanRate"}).asList();
        System.out.println(format("Pending: %d, Count: %d, OneMinuteRate: %.3f, FiveMinuteRate: %.3f, FifteenMinuteRate: %.3f, MeanRate: %.3f",
                pending,
                attributes.get(0).getValue(),
                attributes.get(1).getValue(),
                attributes.get(2).getValue(),
                attributes.get(3).getValue(),
                attributes.get(4).getValue()));
    }

    private Set<Compaction> collectCompactionMetrics(MBeanServerConnection serverConnection) throws MalformedObjectNameException, IOException, ReflectionException, InstanceNotFoundException, AttributeNotFoundException, MBeanException {
        Set<Compaction> sortedSet = new TreeSet<>(new CompactionComparator(this.sort));

        for (ObjectName objectName : serverConnection.queryNames(new ObjectName("org.apache.cassandra.metrics:type=ColumnFamily,keyspace=" + keyspace + ",scope=*,name=PendingCompactions"), null)) {
            String cfName = objectName.getKeyProperty("scope");

            if (!allowedColumnFamilies.contains(cfName)) {
                continue;
            }

            Integer sstableCount = (Integer)serverConnection.getAttribute(
                    new ObjectName("org.apache.cassandra.metrics:type=ColumnFamily,keyspace=" + keyspace + ",scope=" + cfName + ",name=LiveSSTableCount"), "Value");
            Integer pending = (Integer)serverConnection.getAttribute(
                    new ObjectName("org.apache.cassandra.metrics:type=ColumnFamily,keyspace=" + keyspace + ",scope=" + cfName + ",name=PendingCompactions"), "Value");

            sortedSet.add(new Compaction(cfName, pending, sstableCount));
        }

        return sortedSet;
    }
}
