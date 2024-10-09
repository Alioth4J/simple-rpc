package serialize;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工具类
 * 门面模式
 */
public class SerializeSupport {

    private static Map<Class<?>/* 序列化对象类型 */, Serializer/* 序列化器*/> serializerMap = new HashMap<>();
    private static Map<Byte/* 序列化器类型 */, Class<?>/* 序列化对象类型 */> typeMap = new HashMap<>();

    /**
     * 序列化
     * @param entry 序列化的对象
     * @return 序列化后的 byte 数组
     * @param <E> 序列化对象的类型
     */
    public static <E> byte[] serialize(E entry) {
        Serializer<E> serializer = serializerMap.get(entry.getClass());
        if (serializer == null) {
            throw new SerializeException(String.format("Unknown entry class type: %s", entry.getClass().toString()));
        }
        byte[] bytes = new byte[serializer.size(entry) + 1];
        bytes[0] = serializer.type();
        serializer.serialize(entry, bytes, 1, bytes.length - 1);
        return bytes;
    }

    /**
     * 反序列化
     * @param buffer 反序列化的数组
     * @param offset 数组中偏移量
     * @param length 反序列化的长度
     * @param eClass 反序列化得到的对象类型
     * @return 反序列化得到的对象
     * @param <E> 反序列化得到的对象类型
     */
    @SuppressWarnings("unchecked")
    public static <E> E parse(byte[] buffer, int offset, int length, Class<E> eClass) {
        Object entry = serializerMap.get(eClass).parse(buffer, offset, length);
        if (eClass.isAssignableFrom(entry.getClass())) {
            return (E) entry;
        } else {
            throw new SerializeException("Type mismatch");
        }
    }

    public static <E> E parse(byte[] buffer) {
        return parse(buffer, 0, buffer.length);
    }

    public static <E> E parse(byte[] buffer, int offset, int length) {
        byte type = parseEntryType(buffer);
        @SuppressWarnings("unchecked")
        Class<E> eClass = (Class<E>) typeMap.get(type);
        if (null == eClass) {
            throw new SerializeException(String.format("Unknown entry type: %d", type));
        } else {
            return parse(buffer, offset, length, eClass);
        }
    }

    /**
     * 得到序列化器的类型
     * @param buffer 进行反序列化的数组
     * @return 序列化器类型
     */
    private static byte parseEntryType(byte[] buffer) {
        return buffer[0];
    }

}
