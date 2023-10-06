package com.example.services;

import com.example.models.KeyValue;
import com.example.repositories.KeyValueRepository;
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
    public KeyValue setKeyValue(String key, String value) {
        return repository.save(new KeyValue(key,value));
    }

    @Override
    public Optional<KeyValue> getKeyValue(String key) {
        return repository.findById(key);
    }
}
