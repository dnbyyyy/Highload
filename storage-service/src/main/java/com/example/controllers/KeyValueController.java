package com.example.controllers;

import com.example.models.KeyValue;
import com.example.services.KeyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/keys")
public class KeyValueController {

    private final KeyValueService keyValueService;

    @Autowired
    public KeyValueController(KeyValueService keyValueService) {
        this.keyValueService = keyValueService;
    }

    @GetMapping("/{key}")
    public ResponseEntity<Optional<KeyValue>> getKey(@PathVariable String key) {
        Optional<KeyValue> value = keyValueService.getKeyValue(key);
        if (value.isEmpty()) {
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{key}")
    public ResponseEntity<Void> setKey(@PathVariable String key, @RequestBody String value) {
        keyValueService.setKeyValue(key, value);
        return ResponseEntity.ok().build();
    }
}
