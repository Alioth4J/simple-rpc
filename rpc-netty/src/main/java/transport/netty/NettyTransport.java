package transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import transport.InFlightRequests;
import transport.ResponseFuture;
import transport.Transport;
import transport.command.Command;

import java.util.concurrent.CompletableFuture;

/**
 * 使用 Netty 进行网络通信
 */
public class NettyTransport implements Transport {

    // Netty API
    private final Channel channel;

    // 正在处理的请求
    private InFlightRequests inFlightRequests;

    public NettyTransport(Channel channel, InFlightRequests inFlightRequests) {
        this.channel = channel;
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    public CompletableFuture<Command> send(Command request) {
        // 构建返回值
        CompletableFuture<Command> completableFuture = new CompletableFuture<>();

        try {
            // 放入 inFlightRequests 中，表示正在被处理
            inFlightRequests.put(new ResponseFuture(request.getHeader().getRequestId(), completableFuture));
            // 发送
            channel.writeAndFlush(request).addListener((ChannelFutureListener) channelFuture -> {
                // 处理发送失败的情况
                if (!channelFuture.isSuccess()) {
                    completableFuture.completeExceptionally(channelFuture.cause());
                    channel.close();
                }
            });
        } catch (Exception e) {
            // 处理发送异常情况
            inFlightRequests.remove(request.getHeader().getRequestId());
            completableFuture.completeExceptionally(e);
        }
        return completableFuture;
    }

}
