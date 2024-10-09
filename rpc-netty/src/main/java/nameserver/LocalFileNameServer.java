package nameserver;

import serialize.SerializeSupport;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 本地文件 NameServer
 * 本地文件存储 Metadata 序列化后的数组
 */
public class LocalFileNameServer implements NameServer {

    private static final Collection<String> schemes = Collections.singleton("file");

    private File file;

    @Override
    public Collection<String> supportedSchemes() {
        return schemes;
    }

    @Override
    public void connect(URI nameServerUri) {
        if (schemes.contains(nameServerUri.getScheme())) {
            this.file = new File(nameServerUri);
        } else {
            throw new RuntimeException("Unsupported scheme: " + nameServerUri.getScheme());
        }
    }

    @Override
    public synchronized void registerService(String serviceName, URI uri) throws IOException {
        // 读出来，加进去，写回去
        // 读出来
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel fileChannel = raf.getChannel()) {
            FileLock lock = fileChannel.lock();
            try {
                int fileLength = (int) raf.length();
                byte[] bytes;
                Metadata metadata = null;
                bytes = new byte[fileLength];
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                while (buffer.hasRemaining()) {
                    // 因为文件是共享的，所以需要加锁
                    // 使用 FileChannel 加文件锁
                    fileChannel.read(buffer);
                }
                metadata = bytes.length == 0 ? new Metadata() : SerializeSupport.parse(bytes);
                // 加进去
                List<URI> uris = metadata.computeIfAbsent(serviceName, key -> new ArrayList<URI>());
                if (!uris.contains(uri)) {
                    uris.add(uri);
                }
                // 写回去
                bytes = SerializeSupport.serialize(metadata);
                fileChannel.truncate(bytes.length);
                fileChannel.position(0);
                fileChannel.write(ByteBuffer.wrap(bytes));
                fileChannel.force(true);
            } finally {
                lock.release();
            }
        }
    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        // 读出来
        Metadata metadata = null;
        try (RandomAccessFile raf = new RandomAccessFile();
             FileChannel fileChannel = raf.getChannel()) {
            FileLock lock = fileChannel.lock();
            try {
                int fileLength = (int) raf.length();
                byte[] bytes = new byte[fileLength];
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                while (buffer.hasRemaining()) {
                    fileChannel.read(buffer);
                }
                metadata = bytes.length == 0 ? new Metadata() : SerializeSupport.parse(bytes);
            } finally {
                lock.release();
            }
        }
        List<URI> uris = metadata.get(serviceName);
        if (uris == null || uris.isEmpty()) {
            return null;
        }
        // 随机取一个
        return uris.get(ThreadLocalRandom.current().nextInt(uris.size()));
    }

}
