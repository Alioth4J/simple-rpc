package transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import transport.command.Header;

/**
 * 请求反序列化器
 * 在 {@link com.alioth4j.simple-rpc.rpc-netty.transport.netty.NettyServer#newChannelHandlerPipeline NettyServer#newChannelHandlerPipeline} 中被添加，
 * 之后用于 Bootstrap 的创建
 */
public class RequestDecoder extends CommandDecoder {

    @Override
    protected Header decodeHeader(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        return new Header(byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt());
    }

}
