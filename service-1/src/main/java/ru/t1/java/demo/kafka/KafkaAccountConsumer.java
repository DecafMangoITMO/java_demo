package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.service.account.AccountService;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountConsumer {

    private final AccountService accountService;

    @KafkaListener(
            topics = {"${t1.kafka.topic.account}"},
            containerFactory = "kafkaAccountListenerContainerFactory"
    )
    public void listener(@Payload AccountDto accountDto) {
        log.debug("Account consumer: Обработка новых сообщений");
        accountService.create(accountDto);
        log.debug("Account consumer: записи обработаны");
    }

}
