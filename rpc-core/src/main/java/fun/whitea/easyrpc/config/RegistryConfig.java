package fun.whitea.easyrpc.config;

import lombok.Data;


/**
 * RegistryConfig is a configuration class for specifying the settings
 * related to the service registry for RPC.
 */
@Data
public class RegistryConfig {

    /**
     * The type of registry service to use. Default value is "etcd".
     * It specifies the service discovery or registry system used,
     * such as etcd, Zookeeper, or Consul.
     */
    private String registry = "etcd";

    /**
     * The address of the registry service. Default is "<a href="http://localhost:2380">...</a>".
     * This URL points to the registry service that the RPC system will connect to.
     */
    private String address = "http://localhost:2380";

    /**
     * The username for authentication with the registry service.
     * If authentication is required, the username is needed to connect.
     */
    private String username;

    /**
     * The password for authentication with the registry service.
     * This is used in combination with the username to authenticate access.
     */
    private String password;

    /**
     * The timeout duration in milliseconds for registry service requests.
     * Default is 10,000 milliseconds (10 seconds). This controls how long
     * the system will wait for a response from the registry before timing out.
     */
    private Long timeout = 10000L;
}
