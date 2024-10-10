package transport;

public interface TransportServer {

    /**
     * 启动
     * @param requestHandlerRegistry
     * @param port
     * @throws Exception
     */
    void start(RequestHandlerRegistry requestHandlerRegistry, int port) throws Exception;

    /**
     * 停止
     */
    void stop();

}
