package transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import transport.command.Command;
import transport.command.Header;

public abstract class CommandEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
         if (!(msg instanceof Command)) {
             throw new Exception(String.format("Unknown type: %s", msg.getClass().getCanonicalName()));
         }
         Command command = (Command) msg;
         out.writeInt(Integer.BYTES + command.getHeader().length() + command.getPayload().length);
         encodeHeader(ctx, command.getHeader(), out);
         out.writeBytes(command.getPayload());
    }

    protected void encodeHeader(ChannelHandlerContext ctx, Header header, ByteBuf out) throws Exception {
        out.writeInt(header.getRequestId());
        out.writeInt(header.getVersion());
        out.writeInt(header.getType());
    }

}
