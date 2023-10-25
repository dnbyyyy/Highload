package ru.itmo.repositories;

import org.springframework.stereotype.Repository;
import ru.itmo.keyValueStorage.MemTable;
import ru.itmo.models.KeyValue;

import java.util.Optional;

@Repository
public class KeyValueMemTableRepositoryImpl implements KeyValueMemTableRepository{
    private final MemTable<String, String> memTable = new MemTable<>(2);

    @Override
    public Optional<KeyValue> getKeyValue(String key) {
        return Optional.of(new KeyValue(key, memTable.get(key)));
    }

    @Override
    public void setKeyValue(KeyValue keyValue) {
        memTable.set(keyValue.getKey(), keyValue.getValue());
    }
}
