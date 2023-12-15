package ru.itmo.services.redirect;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.itmo.models.KeyValue;

import java.util.ArrayList;

public class RedirectService {

    private final ArrayList<WebClient> webClients = new ArrayList<>();

    @Value("#{'${storage.roots}'.split(',')}")
    private ArrayList<String> rootsList;


    public RedirectService() {
        rootsList.forEach(s -> webClients.add(WebClient.create(s)));
    }

    public Mono<KeyValue> get(String key) {
        return webClients.get(key.hashCode() % webClients.size()).method(HttpMethod.GET)
                .uri("/keys/get?key={key}", key)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KeyValue.class);
    }

    public Mono<Void> set(String key, String value) {
        return webClients.get(key.hashCode() % webClients.size()).method(HttpMethod.POST)
                .uri("/keys/set")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new KeyValue(key, value)), KeyValue.class)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
