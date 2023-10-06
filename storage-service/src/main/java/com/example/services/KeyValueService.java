package com.example.services;


import com.example.models.KeyValue;

import java.util.Optional;

public interface KeyValueService {
    KeyValue setKeyValue(String key, String value);

    Optional<KeyValue> getKeyValue(String key);
}
