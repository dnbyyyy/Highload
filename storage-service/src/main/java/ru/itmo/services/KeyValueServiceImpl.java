package ru.itmo.services;

import ru.itmo.models.KeyValue;
import ru.itmo.repositories.KeyValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KeyValueServiceImpl implements KeyValueService{

    private final KeyValueRepository repository;

    @Autowired
    public KeyValueServiceImpl(KeyValueRepository repository){
        this.repository = repository;
    }

    @Override
    public void setKeyValue(String key, String value) {
        repository.save(new KeyValue(key,value));
    }

    @Override
    public Optional<KeyValue> getKeyValue(String key) {
        return repository.findById(key);
    }
}
