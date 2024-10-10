package client.stubs;

/**
 * RPC 请求封装类
 * 反射调用所需参数
 * 序列化后作为 Command 的 payload
 * @see com.alioth4j.simple-rpc.rpc-netty.transport.command.Command
 */
public class RpcRequest {

    private final String interfaceName;
    private final String methodName;
    private final byte[] serializedArguments;

    public RpcRequest(String interfaceName, String methodName, byte[] serializedArguments) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.serializedArguments = serializedArguments;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public byte[] getSerializedArguments() {
        return serializedArguments;
    }

}
