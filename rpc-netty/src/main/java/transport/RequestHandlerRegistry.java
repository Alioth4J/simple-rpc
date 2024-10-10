package transport;

import spi.ServiceSupport;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC 请求处理器注册处
 * 单例模式
 *
 * @author Alioth4J
 * @date 2024-10-10T22:22:22
 */
public class RequestHandlerRegistry {

    /**
     * 存储 RequestHandler 的集合
     */
    private final Map<Integer/* RequestHandler's type */, RequestHandler/* instance of RequestHandler */> handlerMap = new ConcurrentHashMap<>();

    /**
     * 单例对象
     */
    private static RequestHandlerRegistry instance = null;

    /**
     * 私有构造器
     * 通过 SPI 加载所有 RequestHandler
     */
    private RequestHandlerRegistry() {
        Collection<RequestHandler> requestHandlers = ServiceSupport.loadAll(RequestHandler.class);
        for (RequestHandler requestHandler : requestHandlers) {
            handlerMap.put(requestHandler.type(), requestHandler);
        }
    }

    /**
     * 获得单例对象
     * @return 单例对象
     */
    public static RequestHandlerRegistry getInstance() {
        if (instance == null) {
            synchronized (RequestHandlerRegistry.class) {
                if (instance == null) {
                    instance = new RequestHandlerRegistry();
                }
            }
        }
        return instance;
    }

    /**
     * 获取对应类新规定 RPC 请求处理器
     * @param type RPC 请求处理器的类型
     * @return 对应的 RPC 请求处理器
     */
    public RequestHandler get(int type) {
        return handlerMap.get(type);
    }

}
