package transport;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 正在处理的异步请求
 */
public class InFlightRequests implements Closeable {

    // 既是超时时间，也是定期清理时间
    private final static long TIMEOUT_SEC = 10L;

    // 存储正在处理的 ResponseFuture
    private final Map<Integer/* requestId */, ResponseFuture/* 异步响应 */> futureMap = new ConcurrentHashMap<>();

    // 用于并发控制，背压机制
    private final Semaphore semaphore = new Semaphore(10);

    // 用于定时清理过期的 ResponseFuture
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture scheduledFuture;

    public InFlightRequests() {
        // 定时清理过期的 ResponseFuture
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::removeTimeoutFutures, TIMEOUT_SEC, TIMEOUT_SEC, TimeUnit.SECONDS);
    }

    /**
     * 将 ResponseFuture 放入集合中
     * @param responseFuture 放入的异步响应对象
     * @throws TimeoutException 无法获得信号量
     * @throws InterruptedException 尝试获取信号量时被中断
     */
    public void put(ResponseFuture responseFuture) throws TimeoutException, InterruptedException {
        if (semaphore.tryAcquire(TIMEOUT_SEC, TimeUnit.SECONDS)) {
            futureMap.put(responseFuture.getRequestId(), responseFuture);
        } else {
            throw new TimeoutException();
        }
    }

    /**
     * 移除 ResponseFuture
     * @param requestId 请求 id
     * @return 移除的 ResponseFuture
     */
    public ResponseFuture remove(int requestId) {
        ResponseFuture future = futureMap.remove(requestId);
        if (future != null) {
            semaphore.release();
        }
        return future;
    }

    /**
     * 清理过期的 ResponseFuture
     */
    public void removeTimeoutFutures() {
        futureMap.entrySet().removeIf(entry -> {
            if (System.nanoTime() - entry.getValue().getTimestamp() > TIMEOUT_SEC * 1000000000L) {
                semaphore.release();
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * 关闭
     */
    @Override
    public void close() throws IOException {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }

}
