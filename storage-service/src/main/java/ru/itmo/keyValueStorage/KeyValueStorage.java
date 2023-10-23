package ru.itmo.keyValueStorage;

import java.io.IOException;

public interface KeyValueStorage<K, V> {
    void set(K key, V value) throws IOException;
    V get(K key) throws IOException;
}
