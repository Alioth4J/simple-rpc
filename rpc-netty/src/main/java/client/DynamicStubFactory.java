package client;

import transport.Transport;

/**
 * Stub 工厂的具体实现类
 */
public class DynamicStubFactory implements StubFactory{

    // Stub 的具体实现类的模板源码
    private static final String STUB_SOURCE_TEMPLATE =
                    """
                    package com.alioth4j.simple-rpc.rpc-netty.client.stubs;

                    import com.alioth4j.simple-rpc.rpc-netty.serialize.SerializeSupport;

                    public class %s extends AbstractStub implements %s {
                    
                        @Override
                        public String %s(String arg) {
                            return SerializeSupport.parse(invokeRemote(new RpcRequest(%s, %s, SerializeSupport.serialize(arg))));
                        }
                    
                    }
                    """;

    @Override
    public <T> T createStub(Transport transport, Class<T> serviceClass) {
        // 填充模板
        String stubSimpleName = serviceClass.getSimpleName() + "Stub";
        String classFullName = serviceClass.getName();
        String methodName = serviceClass.getMethods()[0].getName();

        String source = String.format(STUB_SOURCE_TEMPLATE, stubSimpleName, classFullName, methodName, classFullName, methodName);
        // TODO 编译源代码

        ServiceStub stubInstance = (ServiceStub) clazz.newInstance();
        stubInstance.setTransport(transport);
        return (T) stubInstance;
    }

}
