package transport;

import transport.command.Command;

import java.util.concurrent.CompletableFuture;

/**
 * 传输接口，用于发送 RPC 命令
 */
public interface Transport {

    CompletableFuture<Command> send(Command request);

}
