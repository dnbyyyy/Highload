package ru.itmo.repositories;

import org.springframework.stereotype.Repository;
import ru.itmo.models.KeyValue;

import java.security.Key;
import java.util.Optional;


public interface KeyValueMemTableRepository {
    Optional<KeyValue> getKeyValue(String key);
    void setKeyValue(KeyValue keyValue);
}
