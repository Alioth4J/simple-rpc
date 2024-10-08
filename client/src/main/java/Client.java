import java.net.URI;

public class Client {

    public static void main(String[] args) {
        String serviceName = HelloServiceImpl.class.getCanonicalName();
        String name = "Alioth4J";

        // TODO 得到 nameServer, rpcAccessPoint
        URI uri = nameServer.lookupService(serviceName);
        HelloService helloService = rpcAccessPoint.getRemoteService(uri, HelloService.class);
        String response = helloService.hello(name);
        System.out.println(response);
    }

}
