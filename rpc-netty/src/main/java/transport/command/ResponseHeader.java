package transport.command;

import java.nio.charset.StandardCharsets;

/**
 * 响应头
 */
public class ResponseHeader extends Header {

    // 响应码
    private int code;
    // 错误信息
    private String error;

    public ResponseHeader(int code, String error) {
        this.code = code;
        this.error = error;
    }

    public ResponseHeader(int requestId, int version, int type) {
        this(requestId, version, type, Code.SUCCESS.getCode(), null);
    }

    public ResponseHeader(int requestId, int version, int type, int code, String error) {
        super(requestId, version, type);
        this.code = code;
        this.error = error;
    }

    @Override
    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES
                + Integer.BYTES + (error == null ? 0 : error.getBytes(StandardCharsets.UTF_8).length);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
