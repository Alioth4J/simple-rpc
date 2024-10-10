package spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * SPI 类加载器帮助类
 */
public class ServiceSupport {

    // 单例集合
    private static final Map<String, Object> singletonServices = new HashMap<>();

    /**
     * 通过 SPI 加载指定服务类
     * @param serviceClass 服务类
     * @return 服务实例
     * @param <S> 服务的类型
     */
    public static synchronized <S> S load(Class<S> serviceClass) {
        return StreamSupport.stream(ServiceLoader.load(serviceClass).spliterator(), false)
                // 单例模式用单例
                .map(ServiceSupport::singletonFilter)
                .findFirst().orElseThrow(ServiceLoadException::new);
    }

    public static synchronized <S> Collection<S> loadAll(Class<S> service) {
        return StreamSupport.stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static <S> S singletonFilter(S service) {
        if (service.getClass().isAnnotationPresent(Singleton.class)) {
            String className = service.getClass().getCanonicalName();
            Object singletonInstance = singletonServices.putIfAbsent(className, service);
            return singletonInstance == null ? service : (S) singletonInstance;
        } else {
            return service;
        }
    }

}
