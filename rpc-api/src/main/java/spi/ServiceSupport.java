package spi;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * SPI 类加载器帮助类
 */
public class ServiceSupport {

    // 单例集合
    private static final Map<String, Object> singletonServices = new HashMap<>();

    /**
     * 通过 SPI 加载实现类
     * @param service
     * @return
     * @param <S>
     */
    public static synchronized <S> S load(Class<S> service) {
        return StreamSupport.stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter)
                .findFirst().orElseThrow(ServiceLoadException::new);
    }

    @SuppressWarnings("unchecked")
    private static <S> S singletonFilter(S service) {
        if (service.getClass().isAnnotationPresent(Singleton.class)) {
            String className = service.getClass().getCanonicalName();
            Object singletonInstance = singletonServices.putIfAbsent(className, service);
            return singletonInstance == null ? service : singletonInstance;
        } else {
            return service;
        }
    }

}
