package com.example.clientcomponent.service;

import com.example.clientcomponent.entities.KeyValue;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ClientService {
    private final WebClient webClient;

    public ClientService(){
      webClient = WebClient.create("http://localhost:8080");
    }

    public Mono<KeyValue> get(String key){
      return webClient.method(HttpMethod.GET)
          .uri("/keys/get?key={key}", key)
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .bodyToMono(KeyValue.class);
    }

    public Mono<Void> set(String key, String value){
      return webClient.method(HttpMethod.POST)
          .uri("/keys/set")
          .contentType(MediaType.APPLICATION_JSON)
          .body(Mono.just(new KeyValue(key, value)), KeyValue.class)
          .retrieve()
          .bodyToMono(Void.class);
    }

}
