package client;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于获得请求 id
 */
public class RequestIdSupport {

    private final static AtomicInteger nextRequestId = new AtomicInteger(0);

    /**
     * 获得请求 id
     * @return 请求 id
     */
    public static int next() {
        return nextRequestId.getAndIncrement();
    }

}
