package serialize;

/**
 * 序列化器的策略接口
 * @param <T> 序列化对象的类型
 */
public interface Serializer<T> {

    /**
     * 序列化
     * @param entry 序列化对象
     * @param bytes 序列化后存放的数组
     * @param offset 数组偏移量，从这个位置写入数组
     * @param length 序列化长度
     */
    void serialize(T entry, byte[] bytes, int offset, int length);

    /**
     * 反序列化
     * @param bytes 反序列化的数组
     * @param offset 数组偏移量，从这个位置开始反序列化
     * @param length 反序列化长度
     */
    T parse(byte[] bytes, int offset, int length);

    /**
     * 计算序列化后的长度，用于申请对应大小的数组
     * @param entry 待序列化的对象
     * @return 序列化后的长度
     */
    int size(T entry);

    /**
     * 序列化器的类型，对不同的序列化器进行标识
     * @return 序列化器的类型
     */
    byte type();

    /**
     * 得到序列化对象的类型
     * @return
     */
    Class<T> getSerializeClass();

}
