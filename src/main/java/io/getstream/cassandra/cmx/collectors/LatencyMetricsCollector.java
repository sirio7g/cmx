package io.getstream.cassandra.cmx.collectors;

import io.getstream.cassandra.cmx.beans.ColumnFamilyLatency;
import io.getstream.cassandra.cmx.comparators.RateComparator;

import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.format;

public class LatencyMetricsCollector implements MetricsCollector {

    private final String sort;
    private final int limit;
    private final String keyspace;

    private Set<String> allowedColumnFamilies;
    private final Map<String, ColumnFamilyLatency> writeContainer = new HashMap<>();
    private final Map<String, ColumnFamilyLatency> readContainer = new HashMap<>();

    public LatencyMetricsCollector(String keyspace,
                                   Set<String> allowedColumnFamilies,
                                   int limit,
                                   String sort) {
        this.keyspace = keyspace;
        this.allowedColumnFamilies = allowedColumnFamilies;
        this.limit = limit;
        this.sort = sort;
    }

    private void printLatencyMetrics(MBeanServerConnection serverConnection,
                                     String metricsName, Map<String, ColumnFamilyLatency> cfContainer) throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException, IOException {
        System.out.println(format("Metric: %s", metricsName));
        System.out.println(format("%30s %12s %12s %12s %8s %8s %12s %10s %10s", "Column family", "Count(∆)", "Count", "1m", "5m", "15m", "50th", "95th", "99th"));

        int i = 0;
        for (ColumnFamilyLatency columnFamilyLatency : collectLatencyMetrics(serverConnection, metricsName, cfContainer)) {
            System.out.println(format("%30s %12d %12d %12.3f %8.3f %8.3f %12.2f %10.2f %10.2f",
                    columnFamilyLatency.getName(),
                    columnFamilyLatency.getRelativeCount(),
                    columnFamilyLatency.getCount(),
                    columnFamilyLatency.getOneMinuteRate(),
                    columnFamilyLatency.getFiveMinuteRate(),
                    columnFamilyLatency.getFifteenMinuteRate(),
                    columnFamilyLatency.getPercentile50th(),
                    columnFamilyLatency.getPercentile95th(),
                    columnFamilyLatency.getPercentile99th()
            ));
            if (++i >= this.limit) break;
        }
        System.out.println("\n");
    }

    private Set<ColumnFamilyLatency> collectLatencyMetrics(MBeanServerConnection serverConnection, String metricsName, Map<String, ColumnFamilyLatency> cfContainer) throws MalformedObjectNameException, IOException, ReflectionException, InstanceNotFoundException {
        Set<ColumnFamilyLatency> sortedSet = new TreeSet<>(new RateComparator(this.sort));

        for (ObjectName objectName : serverConnection.queryNames(new ObjectName("org.apache.cassandra.metrics:type=ColumnFamily,keyspace=" + keyspace + ",scope=*,name=" + metricsName), null)) {
            String cfName = objectName.getKeyProperty("scope");

            if (!allowedColumnFamilies.contains(cfName)) {
                continue;
            }

            List<Attribute> attributes = serverConnection.getAttributes(objectName, new String[]{"OneMinuteRate", "FiveMinuteRate", "FifteenMinuteRate", "50thPercentile", "95thPercentile", "99thPercentile", "Count"}).asList();


            ColumnFamilyLatency cfLatency = new ColumnFamilyLatency(
                    cfName,
                    (Double)attributes.get(0).getValue(),
                    (Double)attributes.get(1).getValue(),
                    (Double)attributes.get(2).getValue(),
                    (Double)attributes.get(3).getValue(),
                    (Double)attributes.get(4).getValue(),
                    (Double)attributes.get(5).getValue(),
                    (Long)attributes.get(6).getValue()
            );

            if (cfContainer.containsKey(cfName)) {
                cfLatency.setRelativeCount(cfLatency.getCount() - cfContainer.get(cfName).getCount());
            }

            cfContainer.put(cfName, cfLatency);
            sortedSet.add(cfLatency);
        }

        return sortedSet;
    }

    @Override
    public void collect(MBeanServerConnection serverConnection) throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException, IOException {
        this.printLatencyMetrics(serverConnection, "ReadLatency", readContainer);
        this.printLatencyMetrics(serverConnection, "WriteLatency", writeContainer);
    }
}