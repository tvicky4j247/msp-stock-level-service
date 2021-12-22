package co.siegerand.stocklevelservice.util.other;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtil {

    private final String serviceAddress;

    public ServiceUtil(@Value("${server.port}") String port) {
        String serviceAddress1;
        try {
            serviceAddress1 = InetAddress.getLocalHost().getHostName() + "/" +
                    InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            serviceAddress1 = "Unknown host";
        }
        serviceAddress = serviceAddress1;
    }

    public String getServiceUrl() {
        return serviceAddress;
    }
}
