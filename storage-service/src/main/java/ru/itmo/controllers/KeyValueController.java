package ru.itmo.controllers;

import ru.itmo.models.KeyValue;
import ru.itmo.services.KeyValueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/keys")
@Tag(name = "apartments api")
public class KeyValueController {

    private final KeyValueService keyValueService;

    @Autowired
    public KeyValueController(KeyValueService keyValueService) {
        this.keyValueService = keyValueService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getKey(@RequestParam String key) {
        Optional<KeyValue> value = keyValueService.getKeyValue(key);
        if (value.isPresent()) {
            return ResponseEntity.ok(value.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/set")
    public ResponseEntity<?> setKey(@RequestBody KeyValue keyValue) {
        keyValueService.setKeyValue(keyValue.getKey(), keyValue.getValue());
        return ResponseEntity.ok().build();
    }
}
