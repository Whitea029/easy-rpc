package fun.whitea.easyrpc.config;

import fun.whitea.easyrpc.fault.retry.RetryStrategyKeys;
import fun.whitea.easyrpc.loadbalancer.LoadBalancerKeys;
import fun.whitea.easyrpc.serializer.SerializerKeys;
import lombok.Data;


/**
 * RpcConfig is a configuration class for setting up the parameters of an RPC service.
 */
@Data
public class RpcConfig {

    /**
     * The name of the service. Default value is "easy-rpc".
     * This field is used to identify the current RPC service.
     */
    private String name = "easy-rpc";

    /**
     * The version of the service. Default value is "1.0.0".
     * This indicates the version of the RPC service being provided.
     */
    private String version = "1.0.0";

    /**
     * The server host address. Default is "127.0.0.1" (localhost).
     * Specifies the IP address where the RPC server will run.
     */
    private String serverHost = "127.0.0.1";

    /**
     * The server port number. Default value is 9000.
     * Specifies the port number on which the RPC server will listen for incoming connections.
     */
    private int serverPort = 9000;

    /**
     * A flag to enable or disable mock mode. Default is false.
     * If true, mock services may be used instead of real implementations for testing purposes.
     */
    private boolean mock = false;

    /**
     * The serializer type to be used for RPC communication. Default is HESSIAN serialization.
     * Determines the method of serialization for objects sent over the network.
     */
    private String serializer = SerializerKeys.HESSIAN;

    /**
     * The registry configuration for the RPC service.
     * This contains settings related to the service registry, such as address, protocol, etc.
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    private String retryStrategy = RetryStrategyKeys.FIXED_INTERVAL;
}
