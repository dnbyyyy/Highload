package ru.itmo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@RedisHash
@NoArgsConstructor
public class KeyValue {
    @Id
    private String key;
    private String value;

}
