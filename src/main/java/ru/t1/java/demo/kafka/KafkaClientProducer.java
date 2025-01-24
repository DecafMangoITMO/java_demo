package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1.java.demo.dto.ClientDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class KafkaClientProducer {

    private final KafkaTemplate template;

    public void send(Long clientId) {
        try {
            template.sendDefault(UUID.randomUUID().toString(), clientId).get();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }

    public void sendTo(String topic, Object o) {
        try {
            template.send(topic, o).get();
            template.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            o)
                    .get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }
}
