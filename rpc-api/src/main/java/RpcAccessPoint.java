import java.io.Closeable;
import java.net.URI;

public interface RpcAccessPoint extends Closeable {

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
     * @param <T> 服务接口
     */
    <T> T getRemoteService(URI uri, Class<T> serviceClass);

    /**
     * 启动 PRC 框架
     * @return 服务实例，用于程序停止时安全关闭服务
     */
    Closeable startServer() throws Exception;

}
