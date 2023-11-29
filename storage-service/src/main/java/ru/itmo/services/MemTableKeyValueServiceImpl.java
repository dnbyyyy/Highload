package ru.itmo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.itmo.models.KeyValue;
import ru.itmo.repositories.KeyValueMemTableRepository;

import java.util.Optional;

@Service
@Profile("lsm")
public class MemTableKeyValueServiceImpl implements KeyValueService{

    private final KeyValueMemTableRepository keyValueMemTableRepository;

    @Autowired
    public MemTableKeyValueServiceImpl(KeyValueMemTableRepository keyValueMemTableRepository) {
        this.keyValueMemTableRepository = keyValueMemTableRepository;
    }

    @Override
    public void setKeyValue(String key, String value) {
        keyValueMemTableRepository.setKeyValue(new KeyValue(key,value));
    }

    @Override
    public Optional<KeyValue> getKeyValue(String key) {
        return keyValueMemTableRepository.getKeyValue(key);
    }
}
