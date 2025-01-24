package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.LogErrorOrMetrics;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMetricConsumer {

    @KafkaListener(
            topics = {"${t1.kafka.topic.metrics}"},
            containerFactory = "kafkaLogErrorListenerContainerFactory")
    public void listener(@Payload LogErrorOrMetrics object, @Header("X-TYPE") String header) {
        log.debug("Metrics consumer: Обработка новых сообщений");

        if (header.equals("DATA_SOURCE")) {
            log.error("{}", object.getDataSourceErrorLogDto());
        } else {
            log.error("{}", object.getMetricsDto());
        }

        log.debug("Metrics consumer: записи обработаны");
    }

}
