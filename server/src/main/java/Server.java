import java.io.Closeable;
import java.net.URI;

public class Server {

    public static void main(String[] args) {
        String serviceName = HelloServiceImpl.class.getCanonicalName();
        HelloService helloService = new HelloServiceImpl();

        // TODO 得到 rpcAccessPoint
        try (Closeable service = rpcAccessPoint.startServer()) {
            // TODO 得到 nameServer
            URI uri = rpcAccessPoint.addServerProvider(helloService, HelloService.class);
            nameServer.registerService(serviceName, uri);
        }
    }

}
