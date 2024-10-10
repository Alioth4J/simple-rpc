package transport;

import transport.command.Command;

/**
 * RPC 请求处理器
 */
public interface RequestHandler {

    /**
     * 处理请求
     * @param requestCommand RPC 请求命令
     * @return RPC 响应命令
     */
    Command handle(Command requestCommand);

    /**
     * 支持的请求类型
     * @return 支持的请求类型
     */
    int type();

}
