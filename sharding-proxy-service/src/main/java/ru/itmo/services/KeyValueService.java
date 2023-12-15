package ru.itmo.services;


import ru.itmo.models.KeyValue;

import java.util.Optional;

public interface KeyValueService {
    void setKeyValue(String key, String value);

    Optional<KeyValue> getKeyValue(String key);
}
