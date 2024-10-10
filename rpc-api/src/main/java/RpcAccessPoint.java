import spi.ServiceSupport;

import java.io.Closeable;
import java.net.URI;
import java.util.Collection;

/**
 * RPC 服务暴露接口
 */
public interface RpcAccessPoint extends Closeable {

    /**
     * 启动 RPC 框架
     * @return 服务实例，用于程序停止时安全关闭服务
     */
    Closeable startServer() throws Exception;

    /**
     * 服务端注册服务实现的实例
     * @param service 服务实现的实例对象
     * @param serviceClass 服务接口
     * @return 服务地址
     * @param <T> 服务接口类型
     */
    <T> URI addServiceProvider(T service, Class<T> serviceClass);

    /**
     * 客户端获取服务实例
     * @param uri 服务地址
     * @param serviceClass 服务接口
     * @return 服务实例
     * @param <T> Stub 的实例
     */
    <T> T getRemoteService(URI uri, Class<T> serviceClass);

    /**
     * 获取注册中心的引用
     * @param nameServerUri 注册中心的 URI
     * @return 注册中心的引用
     */
    default NameServer getNameServer(URI nameServerUri) {
        // 获得所有注册中心
        Collection<NameServer> nameServers = ServiceSupport.loadAll(NameServer.class);
        // 遍历所有注册中心
        for (NameServer nameServer : nameServers) {
            // 比对是否支持协议
            if (nameServer.supportedSchemes().contains(nameServerUri.getScheme())) {
                nameServer.connect(nameServerUri);
                return nameServer;
            }
        }
        return null;
    }

}
