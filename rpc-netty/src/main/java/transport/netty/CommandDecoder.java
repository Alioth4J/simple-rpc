package transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import transport.command.Command;
import transport.command.Header;

import java.util.List;

public abstract class CommandDecoder extends ByteToMessageDecoder {

    private static final int LENGTH_FILED_LENGTH = Integer.BYTES;

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) {
            return;
        }

        in.markReaderIndex();
        // 总长度
        int length = in.readInt() - LENGTH_FILED_LENGTH;
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        Header header = decodeHeader(ctx, in);
        int payloadLength = length - header.length();
        byte[] payload = new byte[payloadLength];
        in.readBytes(payload);
        out.add(new Command(header, payload));
    }

    protected abstract Header decodeHeader(ChannelHandlerContext ctx, ByteBuf byteBuf);

}
