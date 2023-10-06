package com.example.controllers;

import com.example.services.KeyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keys")
public class KeyValueController {

    @Autowired
    private KeyValueService keyValueService;

    @GetMapping("/{key}")
    public ResponseEntity<String> getKey(@PathVariable String key) {
        String value = keyValueService.getValue(key);
        if (value != null) {
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{key}")
    public ResponseEntity<Void> setKey(@PathVariable String key, @RequestBody String value) {
        keyValueService.setValue(key, value);
        return ResponseEntity.ok().build();
    }
}
