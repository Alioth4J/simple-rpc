import client.StubFactory;
import server.ServiceProviderRegistry;
import spi.ServiceSupport;
import transport.RequestHandlerRegistry;
import transport.Transport;
import transport.TransportClient;
import transport.TransportServer;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * Netty RPC 服务暴露接口
 */
public class NettyRpcAccessPoint implements RpcAccessPoint {

    // 地址信息
    private final String host = "localhost";
    private final int port = 9999;
    private final URI uri = URI.create("rpc://" + host + ":" + port);

    // 通信/传输 服务端和客户端
    private TransportServer server = null;
    private TransportClient client = ServiceSupport.load(TransportClient.class);

    private final Map<URI, Transport> clientMap = new ConcurrentHashMap<>();

    private final StubFactory stubFactory = ServiceSupport.load(StubFactory.class);

    // 服务实现的实例注册处
    private final ServiceProviderRegistry serviceProviderRegistry = ServiceSupport.load(ServiceProviderRegistry.class);

    @Override
    public Closeable startServer() throws Exception {
        if (server == null) {
            server = ServiceSupport.load(TransportServer.class);
            server.start(RequestHandlerRegistry.getInstance(), port);
        }
        return () -> {
            if (server != null) {
                server.stop();
            }
        };
    }

    @Override
    public <T> URI addServiceProvider(T service, Class<T> serviceClass) {
        serviceProviderRegistry.addServiceProvider(serviceClass, service);
        return this.uri;
    }

    @Override
    public <T> T getRemoteService(URI uri, Class<T> serviceClass) {
        Transport transport = clientMap.computeIfAbsent(uri, this::createTransport);
        return stubFactory.createStub(transport, serviceClass);
    }

    /**
     * 根据 URI，由 client 创建出 Transport
     * @param uri 服务地址
     * @return RPC 调用传输接口
     */
    private Transport createTransport(URI uri) {
        try {
            return client.createTransport(new InetSocketAddress(uri.getHost(), uri.getPort()), 30000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (server != null) {
            server.stop();
        }
        client.close();
    }

}
