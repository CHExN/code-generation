package com.chen.codegeneration.demo.reactiveRedisDemo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * Since we may (re)start our application multiple times,
 * we should first remove any data that may still exist from previous executions.
 * We do this with a flushAll() (Redis) server command. Once we’ve flushed any existing data,
 * we create a small Flux, map each coffee name to a Coffee object, and save it to the reactive Redis repository.
 * We then query the repo for all values and display them.
 */
@Component
@RequiredArgsConstructor
public class CoffeeLoader {
    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, Object> redisOps;

    @PostConstruct
    public void loadData() {
        factory.getReactiveConnection().serverCommands().flushAll().thenMany(
                        Flux.just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
                                .map(name -> new Coffee(UUID.randomUUID().toString(), name))
                                .flatMap(coffee -> redisOps.opsForValue().set(coffee.getId(), coffee)))
                .thenMany(redisOps.keys("*")
                        .flatMap(redisOps.opsForValue()::get))
                .subscribe(System.out::println);
    }
}