package client;

import transport.Transport;

public interface StubFactory {

    <T> T createStub(Transport transport, Class<T> serviceClass);

}
