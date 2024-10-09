import java.io.IOException;
import java.net.URI;
import java.util.Collection;

public interface NameServer {

    /**
     * 注册服务
     * @param serviceName 服务名称
     * @param uri 服务地址
     */
    void registerService(String serviceName, URI uri) throws IOException;

    /**
     * 查询服务
     * @param serviceName 服务名称
     * @return 服务地址
     */
    URI lookupService(String serviceName) throws IOException;

    /**
     * 所有支持的协议
     * @return 所有支持的协议
     */
    Collection<String> supportedSchemes();

    /**
     * 连接注册中心
     * @param nameServerUri 注册中心的 URI 地址
     */
    void connect(URI nameServerUri);

}
