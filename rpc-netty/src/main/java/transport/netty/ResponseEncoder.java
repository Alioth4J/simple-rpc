package transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import transport.command.Header;
import transport.command.ResponseHeader;

import java.nio.charset.StandardCharsets;

/**
 * 响应序列化器
 * 在 {@link com.alioth4j.simple-rpc.rpc-netty.transport.netty.NettyServer#newChannelHandlerPipeline NettyServer#newChannelHandlerPipeline} 中被添加，
 * 之后用于 Bootstrap 的创建
 */
public class ResponseEncoder extends CommandEncoder {

    @Override
    protected void encodeHeader(ChannelHandlerContext ctx, Header header, ByteBuf out) throws Exception {
        super.encodeHeader(ctx, header, out);
        if (header instanceof ResponseHeader) {
            ResponseHeader responseHeader = (ResponseHeader) header;
            out.writeInt(responseHeader.getCode());
            int errorLength = header.length() - (Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES);
            out.writeInt(errorLength);
            out.writeBytes(responseHeader.getError() == null ? new byte[0] : responseHeader.getError().getBytes(StandardCharsets.UTF_8));
        } else {
            throw new Exception(String.format("Invalid header type: %s", header.getClass().getCanonicalName()));
        }
    }

}
