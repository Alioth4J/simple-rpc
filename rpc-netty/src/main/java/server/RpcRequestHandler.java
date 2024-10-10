package server;

import client.ServiceTypes;
import client.stubs.RpcRequest;
import serialize.SerializeSupport;
import spi.Singleton;
import transport.RequestHandler;
import transport.command.Code;
import transport.command.Command;
import transport.command.Header;
import transport.command.ResponseHeader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * RPC 请求处理器
 * simple-rpc 的核心类
 */
@Singleton
public class RpcRequestHandler implements RequestHandler, ServiceProviderRegistry {

    /**
     * 存储 serviceProvider 的集合
     */
    private Map<String/* serviceName */, Object/* serviceProvider */> serviceProviders = new HashMap<>();

    @Override
    public synchronized <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider) {
        serviceProviders.put(serviceClass.getCanonicalName(), serviceProvider);
    }

    @Override
    public Command handle(Command requestCommand) {
        // 解析 requestCommand
        Header header = requestCommand.getHeader();
        byte[] payload = requestCommand.getPayload();
        RpcRequest rpcRequest = SerializeSupport.parse(payload);
        // 得到 serviceProvider
        Object serviceProvider = serviceProviders.get(rpcRequest.getInterfaceName());
        if (serviceProvider == null) {
            return new Command(new ResponseHeader(header.getRequestId(), header.getVersion(), type(), Code.NO_PROVIDER.getCode(), "No provider"),
                               new byte[0]);
        }
        // 反射
        String result = null;
        try {
            Method method = serviceProvider.getClass().getMethod(rpcRequest.getMethodName(), String.class);
            String arg = SerializeSupport.parse(rpcRequest.getSerializedArguments());
            result = (String) method.invoke(serviceProvider, arg);
        } catch (NoSuchMethodException e) {
            return new Command(new ResponseHeader(header.getRequestId(), header.getVersion(), type(), Code.NO_PROVIDER.getCode(), "No method"),
                               new byte[0]);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return new Command(new ResponseHeader(header.getRequestId(), header.getVersion(), type(), Code.UNKNOWN_ERROR.getCode(), e.getMessage()),
                               new byte[0]);
        }

        return new Command(new ResponseHeader(header.getRequestId(), header.getVersion(), type()), SerializeSupport.serialize(result));
    }

    @Override
    public int type() {
        return ServiceTypes.TYPE_RPC_REQUEST;
    }

}
