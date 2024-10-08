package transport.command;

/**
 * 请求/响应的头部信息
 */
public class Header {

    // 请求 id
    private int requestId;
    // 通信版本号
    private int version;
    // 命令的类型，接收方据此路由到相应的处理类中
    private int type;

    public Header() {
    }

    public Header(int requestId, int version, int type) {
        this.requestId = requestId;
        this.version = version;
        this.type = type;
    }

    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
