package transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import transport.command.Header;

/**
 * 请求序列化器
 * 在 {@link com.alioth4j.simple-rpc.rpc-netty.transport.netty.NettyClient#newChannelHandlerPipeline NettyClient#newChannelHandlerPipeline} 中被添加，
 * 之后用于 Bootstrap 的创建
 */
public class RequestEncoder extends CommandEncoder {

    @Override
    protected void encodeHeader(ChannelHandlerContext ctx, Header header, ByteBuf out) throws Exception {
        super.encodeHeader(ctx, header, out);
    }

}
