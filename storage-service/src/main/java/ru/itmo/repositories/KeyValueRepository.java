package ru.itmo.repositories;

import org.springframework.context.annotation.Profile;
import ru.itmo.models.KeyValue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Profile("redis")
@Repository
public interface KeyValueRepository extends CrudRepository<KeyValue, String> {
}

