package transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import transport.RequestHandler;
import transport.RequestHandlerRegistry;
import transport.command.Command;

public class RequestInvocation extends SimpleChannelInboundHandler<Command> {

    private final RequestHandlerRegistry requestHandlerRegistry;

    RequestInvocation(RequestHandlerRegistry requestHandlerRegistry) {
        this.requestHandlerRegistry = requestHandlerRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) throws Exception {
        RequestHandler handler = requestHandlerRegistry.get(msg.getHeader().getType());
        if (handler != null) {
            Command response = handler.handle(msg);
            if (response != null) {
                ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()) {
                        ctx.channel().close();
                    }
                });
            }
        } else {
            throw new Exception(String.format("No handler found for request with type : %d", msg.getHeader().getType()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }

}
