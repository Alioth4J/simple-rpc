package transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import transport.command.Header;
import transport.command.ResponseHeader;

import java.nio.charset.StandardCharsets;

/**
 * 响应反序列化器
 * 在 {@link com.alioth4j.simple-rpc.rpc-netty.transport.netty.NettyClient#newChannelHandlerPipeline NettyClient#newChannelHandlerPipeline} 中被添加，
 * 之后用于 Bootstrap 的创建
 */
public class ResponseDecoder extends CommandDecoder {

    @Override
    protected Header decodeHeader(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        int requestId = byteBuf.readInt();
        int version = byteBuf.readInt();
        int type = byteBuf.readInt();
        int code = byteBuf.readInt();
        int errorLength = byteBuf.readInt();
        byte[] errorBytes = new byte[errorLength];
        byteBuf.readBytes(errorBytes);
        String error = new String(errorBytes, StandardCharsets.UTF_8);
        return new ResponseHeader(requestId, version, type, code, error);
    }

}
