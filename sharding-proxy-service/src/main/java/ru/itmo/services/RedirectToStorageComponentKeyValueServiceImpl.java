package ru.itmo.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.itmo.models.KeyValue;
import ru.itmo.services.redirect.RedirectService;
;

import java.util.Optional;


@Profile("lsm")
@Service
public class RedirectToStorageComponentKeyValueServiceImpl implements KeyValueService {

    private static final Logger logger = LogManager.getLogger();
    private final RedirectService redirectService = new RedirectService();

    @Override
    public void setKeyValue(String key, String value) {
        redirectService.set(key, value).block();
        logger.info("Set Key Value Pair");
    }

    @Override
    public Optional<KeyValue> getKeyValue(String key) {
        KeyValue keyValue = redirectService.get(key).block();
        logger.info(keyValue.getKey() + ": " + keyValue.getValue());
        return Optional.of(keyValue);
    }
}
