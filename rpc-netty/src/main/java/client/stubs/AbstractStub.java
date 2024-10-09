package client.stubs;

import client.RequestIdSupport;
import client.ServiceStub;
import client.ServiceTypes;
import serialize.SerializeSupport;
import transport.Transport;
import transport.command.Code;
import transport.command.Command;
import transport.command.Header;
import transport.command.ResponseHeader;

import java.util.concurrent.ExecutionException;

public abstract class AbstractStub implements ServiceStub {

    protected Transport transport;

    /**
     * 进行远程调用
     * @param request RPC 请求封装类
     * @return RPC 返回结果
     */
    protected byte[] invokeRemote(RpcRequest request) {
        // 组 Command
        Header header = new Header(RequestIdSupport.next(), 1, ServiceTypes.TYPE_RPC_REQUEST);
        byte[] payload = SerializeSupport.serialize(request);
        Command requestCommand = new Command(header, payload);
        // 利用 transport 发送 requestCommand，得到响应 responseCommand
        Command responseCommand = null;
        try {
            responseCommand = transport.send(requestCommand).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        // 判断响应是否成功，成功返回，失败抛异常
        ResponseHeader responseHeader = (ResponseHeader) responseCommand.getHeader();
        if (responseHeader.getCode() == Code.SUCCESS.getCode()) {
            return responseCommand.getPayload();
        } else {
            throw new RuntimeException(responseHeader.getError());
        }
    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }

}
