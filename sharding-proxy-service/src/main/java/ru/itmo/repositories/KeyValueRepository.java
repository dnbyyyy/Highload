package ru.itmo.repositories;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.models.KeyValue;

@Profile("cluster")
@Repository
public interface KeyValueRepository extends CrudRepository<KeyValue, String> {
}

