package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.dto.MetricsDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.kafka.KafkaMetricProducer;
import ru.t1.java.demo.service.client.ClientService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;

    private final KafkaMetricProducer producer;

    @LogException
    @Track
    @GetMapping(value = "/client")
    @HandlingResult
    public void doSomething() throws IOException, InterruptedException {
        try {
            clientService.parseJson();
        Thread.sleep(3000L);
        } catch (Exception e) {
            log.info("Catching exception from ClientController");
            throw new ClientException();
        }
    }

    @PostMapping("/metric")
    public void test(@RequestBody MetricsDto metricsDto) {
        producer.send(metricsDto);
    }

}
