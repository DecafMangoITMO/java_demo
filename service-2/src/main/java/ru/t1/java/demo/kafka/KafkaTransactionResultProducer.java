package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1.java.demo.dto.TransactionResultDto;

@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionResultProducer {

    @Value("${t1.kafka.topic.transaction_result}")
    private String topic;

    private final KafkaTemplate template;

    public void send(TransactionResultDto transactionResultDto) {
        template.send(topic, transactionResultDto);
    }

}
