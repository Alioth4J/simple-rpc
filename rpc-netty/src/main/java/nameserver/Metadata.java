package nameserver;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

/**
 * 本地文件 NameServer 存储的数据
 * 本质上是一个 HashMap（实际上也是，当前仅仅是做了一次封装）
 * @see LocalFileNameServer
 */
public class Metadata extends HashMap<String/* 服务名 */, List<URI>/* 服务提供者的 URI 列表 */> {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Metadata:").append("\n");
        for (Entry<String, List<URI>> entry : entrySet()) {
            sb.append("\t").append("Classname: ").append(entry.getKey()).append("\n");
            sb.append("\t").append("URIs: ").append("\n");
            for (URI uri : entry.getValue()) {
                sb.append("\t\t").append(uri).append("\n");
            }
        }
        return sb.toString();
    }

}
