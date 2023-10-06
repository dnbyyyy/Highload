package ru.itmo.repositories;

import ru.itmo.models.KeyValue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyValueRepository extends CrudRepository<KeyValue, String> {
}
