# Cassandra's command-line Metrics Collector

```
usage: java -jar cmx-1.0-SNAPSHOT.jar -k <keyspace> -m <metric_name>
            [options]
Cassandra's command-line Metrics Collector
 -c,--cf <arg>              Column families, comma separated
 -k,--keyspace <arg>        Required: keyspace
 -l,--limit <arg>           Display only 'limit' results. Default: 10
 -m,--metric <arg>          Required. Metric: Latency, Compaction.
 -n,--n <arg>               RMI endpoint: ip:port. Default localhost:7199
 -s,--sort <arg>            Sort: OneMinuteRate, FiveMinuteRate,
                            FifteenMinuteRate. Default: OneMinuteRate
 -t,--time-interval <arg>   Polling interval (in seconds). Default: 60
                            seconds
```