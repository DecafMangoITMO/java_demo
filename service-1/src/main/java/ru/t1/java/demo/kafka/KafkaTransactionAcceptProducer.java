package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1.java.demo.dto.TransactionAcceptDto;

@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionAcceptProducer {

    @Value("${t1.kafka.topic.transaction_accept}")
    private String topic;

    private final KafkaTemplate<String, TransactionAcceptDto> template;

    public void send(TransactionAcceptDto transactionAcceptDto) {
        template.send(topic, transactionAcceptDto);
    }

}
