package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.dto.LogErrorOrMetrics;
import ru.t1.java.demo.dto.MetricsDto;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Slf4j
public class KafkaMetricProducer {

    @Value("${t1.kafka.topic.metrics}")
    private String metricsTopic;

    private final KafkaTemplate template;

    public void send(DataSourceErrorLogDto dataSourceErrorLogDto) {
        ProducerRecord<String, LogErrorOrMetrics> record = new ProducerRecord<>(metricsTopic, new LogErrorOrMetrics(dataSourceErrorLogDto, null));
        record.headers().add("X-TYPE", "DATA_SOURCE".getBytes(StandardCharsets.UTF_8));
        template.send(record);
    }

    public void send(MetricsDto metrics) {
        ProducerRecord<String, LogErrorOrMetrics> record = new ProducerRecord<>(metricsTopic, new LogErrorOrMetrics(null, metrics));
        record.headers().add("X-TYPE", "METRICS".getBytes(StandardCharsets.UTF_8));
        template.send(record);
    }

}
