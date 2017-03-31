package io.getstream.cassandra.cmx;

import io.getstream.cassandra.cmx.collectors.MetricsCollector;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;

public class CollectorMain {

    private static final String SERVICE_URL = "service:jmx:rmi:///jndi/rmi://%s/jmxrmi";

    private MBeanServerConnection serverConnection;

    public CollectorMain(MetricsCollector collector,
                         String nodeEndpoint,
                         int timeInterval) {
        int timeInterval1 = timeInterval * 1000;

        System.out.print(format("Connecting to %s ...", format(SERVICE_URL, nodeEndpoint)));
        try {
            JMXServiceURL url = new JMXServiceURL(format(SERVICE_URL, nodeEndpoint));

            try (JMXConnector connection = JMXConnectorFactory.connect(url, null)) {
                serverConnection = connection.getMBeanServerConnection();
                System.out.println("Connected!");

                collector.collect(serverConnection);

                while (!Thread.currentThread().isInterrupted()) {
                    printSysInfo();
                    collector.collect(serverConnection);
                    Thread.sleep(timeInterval1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printSysInfo() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, IOException {
        List<Attribute> attributes = serverConnection.getAttributes(
                new ObjectName("java.lang:type=OperatingSystem"),
                new String[]{"ProcessCpuLoad", "SystemCpuLoad", "SystemLoadAverage"}).asList();
        System.out.println(format("Date: %s, ProcessCpuLoad: %.3f,  SystemCpuLoad: %.3f, SystemLoadAverage: %.2f\n",
                new Date().toString(),
                attributes.get(0).getValue(),
                attributes.get(1).getValue(),
                attributes.get(2).getValue()));
    }
}