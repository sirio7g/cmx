package io.getstream.cassandra.cmx.collectors;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import java.io.IOException;

public interface MetricsCollector {

    void collect(MBeanServerConnection serverConnection) throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException, IOException, AttributeNotFoundException, MBeanException;
}
